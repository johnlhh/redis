import redis.clients.jedis.Jedis;

/**
 * Created by luohuahua on 17/5/9.
 */
public class RedisTest {


    public static void main(String[] args) {


        testTimeout();
    }


    /**
     * redis.conf中的参数timeout 默认为300（秒） 如果设置为0则忽略超时问题。
     * # 此参数为设置客户端空闲超过timeout，服务端会断开连接，为0则服务端不会主动断开连接，不能小于0。
     * 假设  设置了 timeout 10(秒)
     */
    public static void testTimeout() {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.9.165", 6389);
        System.out.println("Connection to server sucessfully");
        //设置 redis 字符串数据
        jedis.set("runoobkey", "Redis tutorial");
        // 获取存储的数据并输出
        try {
            long timeout = 1000;
            Thread.sleep(timeout);   //当timeout > 10000 时则抛异常 unexpected end of stream
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stored string in redis:: " + jedis.get("runoobkey"));
    }
}
