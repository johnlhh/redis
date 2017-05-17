package publish;

import redis.clients.jedis.Jedis;

/**
 * Created by luohuahua on 17/5/10.
 */
public class TestPublish {

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("192.168.9.165", 6389);
        jedis.publish("redisChatTest", "我是天才");
        Thread.sleep(5000);
        jedis.publish("redisChatTest", "我牛逼");
        Thread.sleep(5000);
        jedis.publish("redisChatTest", "哈哈");
    }
}
