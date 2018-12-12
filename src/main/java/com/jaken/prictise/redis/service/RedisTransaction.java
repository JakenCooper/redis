package com.jaken.prictise.redis.service;

import com.jaken.prictise.redis.util.JedisTools;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RedisTransaction {
    /***
     *  classic "dirty-read" problem
     */
    private static class Counter implements Callable<String> {
        private Jedis jedis;
        private Integer index;
        private boolean outerobj = false;
        public Counter(Integer index) {
            jedis = JedisTools.getInstance().getJedis();
            this.index = index;
        }
        public Counter(Jedis jedis,Integer index){
            this.jedis = jedis;
            this.index = index;
            this.outerobj = true;
        }
        public String call() {
            try {
                Pipeline pipeline = jedis.pipelined();
//                pipeline.multi();
                System.out.println("number "+index+" callable ready to execute .. ");
//                Response<String> beforeresponse = pipeline.get("test-counter");
//                System.out.println("before === "+beforeresponse.get());
//                pipeline.incr("test-counter");
//                Response<String> middleresponse = pipeline.get("test-counter");
//                System.out.println("middle === "+middleresponse.get());
//                pipeline.incrBy("test-counter",-1);
//                Response<String> afterresponse = pipeline.get("test-counter");
//                System.out.println("after === "+afterresponse.get());
                pipeline.incr("test-counter");
                try {
                    if(index == 1) {
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pipeline.incrBy("test-counter",-1);
                Response<String> response = pipeline.get("test-counter");
//                pipeline.exec();
                pipeline.sync();
                System.out.println(response.get());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(!outerobj){
                    JedisTools.getInstance().returnJedis(jedis);
                }
            }
            return "success";
            //end of run --
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

    private static void counter_init(){
        Jedis jedis = JedisTools.getInstance().getJedis();
        jedis.del("test-counter");
        jedis.set("test-counter","0");
        JedisTools.getInstance().returnJedis(jedis);
    }

    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        // counter realted
        counter_init();
        Jedis jedisObj = JedisTools.getInstance().getJedis();
        List<Callable<String>> callables = new ArrayList<>();
        for(int index = 1 ;index<=20; index++){
            callables.add(new Counter(index));
        }
        List<Future<String>> futures = executorService.invokeAll(callables);
        for(Future<String> future : futures){
            if(future.isDone()){
                System.out.println(future.get());
            }
        }
    }
}

