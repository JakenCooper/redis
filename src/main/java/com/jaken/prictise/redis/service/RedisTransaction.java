package com.jaken.prictise.redis.service;

import com.jaken.prictise.redis.util.JedisTools;
import redis.clients.jedis.Jedis;

public class RedisTransaction {
    private static class Counter implements  Runnable{
        @Override
        public void run() {

        }
    }

    private static void init(){
        Jedis jedis = JedisTools.getInstance().getJedis();
        jedis.del("shop:");
        jedis.del("package-user:1");
        jedis.del("package-user:2");
        jedis.zadd("shop:",new Long(15).doubleValue(),"卫生纸");
        jedis.zadd("shop:",new Long(30).doubleValue(),"笔记本");
        jedis.zadd("package-user:1",new Long(22).doubleValue(),"可乐300ml");
        jedis.zadd("package-user:2",new Long(76).doubleValue(),"创可贴");
        JedisTools.getInstance().returnJedis(jedis);
    }
}

