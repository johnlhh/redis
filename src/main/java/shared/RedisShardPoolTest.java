package shared;

/**
 * Created by luohuahua on 17/5/11.
 */


import java.util.ArrayList;
import java.util.List;


import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

public class RedisShardPoolTest {

    private static ShardedJedisPool pool;

    /**
     * 如果 抛异常Could not get a resource from the pool
     * 1 检查每台redis服务能否正常访问
     * 2 远程是否能登入redis服务
     * 3 检查设置timeout是否过短
     * 4 pool 需要设置为private static 确保建立连接池线程安全
     */
    static {
        JedisPoolConfig config = new JedisPoolConfig();//Jedis池配置
        config.setMaxTotal(500);//最大活动的对象个数
        config.setMaxWaitMillis(1000 * 10);//获取对象时最大等待时间
        config.setMaxIdle(1000 * 60);//对象最大空闲时间
        config.setTestOnBorrow(true);
        String hostA = "192.168.9.165";
        int portA = 6389;
        String hostB = "192.168.9.90";
        int portB = 6379;
        List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(2);
        JedisShardInfo infoA = new JedisShardInfo(hostA, portA);
        //infoA.setPassword("123");
        JedisShardInfo infoB = new JedisShardInfo(hostB, portB);
        infoB.setPassword("123");
        jdsInfoList.add(infoA);
        jdsInfoList.add(infoB);

        pool = new ShardedJedisPool(config, jdsInfoList, Hashing.MURMUR_HASH,
                Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            String key = generateKey();
            //key += "{aaa}";
            ShardedJedis jds = null;
            try {
                jds = pool.getResource();
                System.out.println(key + ":" + jds.getShard(key).getClient().getHost());
                System.out.println(jds.set(key, "1111111111111111111111111111111"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.returnResource(jds);
            }
        }
    }

    private static int index = 1;

    public static String generateKey() {
        return String.valueOf(Thread.currentThread().getId()) + "_" + (index++);
    }
}
