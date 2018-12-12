package com.jaken.prictise.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTools {
    private static final String JEDIS_HOST = "192.168.1.14";
    private static final Integer JEDIS_PORT = 6379;
    private static JedisPool jedisPool = null;

    private static JedisPool getPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(3000);
        config.setMaxIdle(50000);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);

        jedisPool = new JedisPool(config,JEDIS_HOST,JEDIS_PORT,3000);

        return jedisPool;
    }

    private JedisTools jedisTools;
    public static JedisTools getInstance(){
        if(jedisPool == null){
            jedisPool = getPool();
        }
        return new JedisTools();
    }

    public Jedis getJedis(){
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis){
        jedisPool.returnResource(jedis);
    }
}
