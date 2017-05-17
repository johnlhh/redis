package tranction;

/**
 * Created by luohuahua on 17/5/10.
 */

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TestTransaction {
    //模拟信用卡消费和还款
    public static void main(String[] args) {
        TestTransaction t = new TestTransaction();
        boolean retValue = false;
        boolean Interrupted = false;

        try {
            retValue = t.transMethod(100);
        } catch (InterruptedException e) {
            Interrupted = true;
            System.out.println("事务被打断，请重新执行!");
        } finally {
            if (retValue) {
                System.out.println("使用信用卡消费成功！");
            } else {
                if (!Interrupted) {
                    System.out.println("使用信用卡消费失败！余额不足！");
                }
            }
        }
    }

    /**
     * 通俗点讲，watch命令就是标记一个键，如果标记了一个键，
     * 在提交事务前如果该键被别人修改过，那事务就会失败，这种情况通常可以在程序中
     * 重新再尝试一次。
     * <p>
     * 首先标记了balance，然后检查余额是否足够，不足就取消标记，并不做扣减；
     * 足够的话，就启动事务进行更新操作。
     * 如果在此期间键balance被其他人修改，拿在提交事务(执行exec)时就会报错，
     * 程序中通常可以捕获这类错误再重新执行一次，直到成功。
     */
    private boolean transMethod(int amount) throws InterruptedException {

        System.out.println("您使用信用卡预付款" + amount + "元");

        Jedis jedis = new Jedis("192.168.9.165", 6389);

        int balance = 1000;//可用余额
        int debt;//欠额
        int amtToSubtract = amount;//实刷额度

        jedis.set("balance", String.valueOf(balance));
        jedis.watch("balance");
        jedis.set("balance", "1100");//此句不该出现，为了模拟其他程序已经修改了该条目
        balance = Integer.parseInt(jedis.get("balance"));
        if (balance < amtToSubtract) {//可用余额小于实刷金额，拒绝交易
            jedis.unwatch();
            System.out.println("可用余额不足！");
            return false;
        } else {//可用余额够用的时候再去执行扣费操作
            System.out.println("扣费transaction事务开始执行...");
            Transaction transaction = jedis.multi();
            transaction.decrBy("balance", amtToSubtract);//余额减去amtToSubtract的钱数
            transaction.incrBy("debt", amtToSubtract);//信用卡欠款增加amtToSubtract的钱数
            List<Object> result = transaction.exec();//执行事务

            if (result == null) {//事务提交失败，说明在执行期间数据被修改过

                System.out.println("扣费transaction事务执行中断...");
                throw new InterruptedException();

            } else {//事务提交成功
                balance = Integer.parseInt(jedis.get("balance"));
                debt = Integer.parseInt(jedis.get("debt"));
                System.out.println("扣费transaction事务执行结束...");

                System.out.println("您的可用余额:" + balance);
                System.out.println("您目前欠款:" + debt);

                return true;
            }
        }
    }

}
