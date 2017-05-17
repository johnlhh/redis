package replicate;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *主：192.168.9.165 6379
 *redis.conf内容如下:
 *protected-mode no
 *daemonize yes
 *
 *从：192.168.9.90 6379
 *redis.conf内容如下:
 *port 6379
 *protected-mode no
 *daemonize yes
 *
 *sentinel monitor:
 *sentinel.conf内容如下:
 *protected-mode no
 *port 26379
 *sentinel monitor mymaster 192.168.9.90 6379 1
 *sentinel down-after-milliseconds mymaster 5000
 *sentinel failover-timeout mymaster 900000
 *sentinel parallel-syncs mymaster 2
 *
 *
 * Created by luohuahua on 17/5/12.
 */
public class ReplicateTest {

    static {
        // redis 属性配置 start
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(5000);
        config.setMaxIdle(256);
        config.setMaxWaitMillis(5000L);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(60000L);
        config.setTimeBetweenEvictionRunsMillis(3000L);
        config.setNumTestsPerEvictionRun(-1);

        // redis 属性配置 end

        Set<String> sentinels = new HashSet<String>();
        sentinels.add("192.168.9.61:26379"); // 此处放置ip及端口为 sentinel
        // 服务地址，如果有多个sentinel 则逐一add即可
        jedisPool = new JedisSentinelPool("mymaster", sentinels, config);
    }

    private static JedisSentinelPool jedisPool;

    public static void main(String[] args) {
        HostAndPort hostAndPort = jedisPool.getCurrentHostMaster();
        System.out.println(hostAndPort.getHost());
        Jedis jedis = jedisPool.getResource();

        String s = jedis.get("a");
        System.out.println(s);
        String s1 = jedis.get("b");
        System.out.println(s1);
    }
}
