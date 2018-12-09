package com.jaken.prictise.redis.service;

import com.jaken.prictise.redis.entity.Article;
import com.jaken.prictise.redis.util.CommonUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.net.URLEncoder;
import java.util.*;

public class ArticleService {

    private Jedis jedis;

    public ArticleService(Jedis jedis) {
        this.jedis = jedis;
    }


    public String addString(String key,String value){
        return jedis.set(key,value);
    }

    public String addArticle(Article article){
        System.out.println("ready to add article");
        String result = jedis.hmset("article:"+article.getId(), CommonUtils.redisConvert(article));
        return result;
    }

    public List<String> batchAddArticles(List<Article> articles){
        List<String> resultList =new ArrayList<String>();
        for(Article article : articles){
            String result = jedis.hmset("article:"+article.getId(),CommonUtils.redisConvert(article));
            resultList.add(result);
        }
        return resultList;
    }

    public List<Article> getAllArticles(){
        Set<String> articleKeys = jedis.keys("article:*");
        List<Article> articles = new ArrayList<Article>();
        for(String articleKey : articleKeys){
            Map<String,String> articleMap = jedis.hgetAll(articleKey);
            Article article = new Article();
            article = CommonUtils.redisConvert(article,articleMap);
            articles.add(article);
        }
        Collections.sort(articles);;
        return articles;
    }


    public void test(Set<String> lists){
        Double score = new Long(System.currentTimeMillis()).doubleValue();
        Map<String,Double> targetMap = new HashMap<String,Double>();
        for(String val : lists){
            targetMap.put(val,score);
        }
        Pipeline pipeline = jedis.pipelined();
        pipeline.multi();
        pipeline.exec();
        pipeline.sync();

        jedis.zadd("article-vote-user",targetMap);
    }

    public void scantest1(){
        List<String> keyList =new ArrayList<String>();
        ScanParams sp = new ScanParams();
        sp.match("*:1*");
        ScanResult<String> sr =jedis.scan(0,sp);
        keyList.addAll(sr.getResult());
        while(sr.getCursor()!=0){
            keyList.addAll(sr.getResult());
            sr=jedis.scan(sr.getCursor(),sp);
        }
        System.out.println(keyList.size());
        System.out.println(keyList);
    }

    public String vote(String articleId,String loginname){
        if(jedis.sismember("article-vote:"+articleId,loginname)){
            return "voted";
        }
        jedis.sadd("article-vote:"+articleId,loginname);
        Double oriscore = jedis.zscore("article-score",articleId);
        if(oriscore == null) {
            jedis.zadd("article-score", new Long(System.currentTimeMillis()).doubleValue(), articleId);
        }else{
            jedis.zadd("article-score",oriscore+20,articleId);
        }
        return "success";
    }

    public static void main(String[] args) throws Exception{
        String oristr= "张";
        System.out.println((int)"张".charAt(0));
        System.out.println((char)20005);
//        String target = "\u5f20";
        String target = "\u4E25\u5f20";
        System.out.println(target);
        System.out.println(URLEncoder.encode(oristr,"UTF-8"));
        System.out.println(URLEncoder.encode(oristr,"GBK"));
    }
}
