package com.jaken.prictise.redis.service;

import com.jaken.prictise.redis.entity.Article;
import com.jaken.prictise.redis.util.CommonUtils;
import com.jaken.prictise.redis.util.JedisTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

public class ArticleServiceTester {

    private ArticleService articleService;
    private Jedis jedis;

    @Before
    public void beforeTest(){
        jedis = JedisTools.getInstance().getJedis();
        articleService = new ArticleService(jedis);
    }

    @Test
    public void addstring(){
        String result = articleService.addString("user:1","jaken");
        System.out.println("executed result ==== "+result);
    }

    @Test
    public void addarticle(){
        Article article = new Article(CommonUtils.genUUID(),"我的世界","我的世界充满了希望和光明");
        String result = articleService.addArticle(article);
        System.out.println("executed result ==== "+result);
    }

    @Test
    public void batchaddarticle(){
        List<Article> articles = new ArrayList<Article>();
        long begintime = System.currentTimeMillis();
        for(int i=0; i<200;i++){
            Article article = new Article(String.valueOf((i+1)),"title-"+(i+1),"content-"+(i+1));
            articles.add(article);
        }
        long middletime = System.currentTimeMillis();
        System.out.println("until middle time: "+(middletime-begintime));
        List<String> results = articleService.batchAddArticles(articles);
        long endtime = System.currentTimeMillis();
        System.out.println("until end time: "+(endtime-middletime));
    }

    @Test
    public void getallarticles(){
        List<Article> articles = articleService.getAllArticles();
        int index = 0;
        for(Article article: articles){
           if(article.getId().indexOf("20000")!=-1){
               System.out.println("index === "+index+"   "+article.getId()+"-"+article.getTitle());
           }
           index++;
        }
        System.out.println(articles.get(19999).getId()+"-"+articles.get(19999).getTitle());
    }


    @Test
    public void testtest(){
        /*List<String> valList = new ArrayList<String>();
        for(int i=0;i<5;i++){
            String val = "article-vote-user:"+i;

        }*/
        articleService.scantest1();
    }

    @Test
    public void vote(){
        articleService.vote("123","zmxia");
    }

    @After
    public void afterTest(){
        JedisTools.getInstance().returnJedis(jedis);
        articleService = null;
    }
}
