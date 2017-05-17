package publish;

import redis.clients.jedis.Jedis;

/**
 * Created by luohuahua on 17/5/10.
 */
public class TestSubscribe {


    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.9.165", 6389);
        RedisMsgPubSubListener listener = new RedisMsgPubSubListener();
        jedis.subscribe(listener, "redisChatTest");
    }
}
