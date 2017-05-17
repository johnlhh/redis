package cluster;

/**
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * Created by luohuahua on 17/5/11.
 */

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class TestCluster {

    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(500);//最大活动的对象个数
        config.setMaxWaitMillis(1000 * 10);//获取对象时最大等待时间
        config.setMaxIdle(1000 * 30);//对象最大空闲时间
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        HostAndPort hostAndPort1 = new HostAndPort("192.168.9.165", 7000);
        HostAndPort hostAndPort2 = new HostAndPort("192.168.9.165", 7001);
        HostAndPort hostAndPort3 = new HostAndPort("192.168.9.165", 7002);
        HostAndPort hostAndPort4 = new HostAndPort("192.168.9.165", 7003);
        HostAndPort hostAndPort5 = new HostAndPort("192.168.9.165", 7004);
        HostAndPort hostAndPort6 = new HostAndPort("192.168.9.165", 7005);
        nodes.add(hostAndPort1);
        nodes.add(hostAndPort2);
        nodes.add(hostAndPort3);
        nodes.add(hostAndPort4);
        nodes.add(hostAndPort5);
        nodes.add(hostAndPort6);
        JedisCluster jedisCluster = new JedisCluster(nodes, config);//JedisCluster中默认分装好了连接池.
        //redis内部会创建连接池，从连接池中获取连接使用，然后再把连接返回给连接池

        jedisCluster.set("a", "hello world");
        String string = jedisCluster.get("a");

        System.out.println(string);
    }

}
