package com.jaken.prictise.redis.entity;

import java.util.Date;
import java.util.Map;

public class Article implements Comparable<Article>{

    @RedisMapper
    private String id;
    @RedisMapper
    private String title;
    @RedisMapper
    private Integer voted = 0;
    @RedisMapper
    private String content;
    @RedisMapper
    private Date createDate;

    private String nonRedisEle ="NON_REDIS";

    public Article() {
    }

    public Article(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = new Date();
    }

    public static Map<String,String> convert(Article article){
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVoted() {
        return voted;
    }

    public void setVoted(Integer voted) {
        this.voted = voted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getNonRedisEle() {
        return nonRedisEle;
    }

    public void setNonRedisEle(String nonRedisEle) {
        this.nonRedisEle = nonRedisEle;
    }

    @Override
    public int compareTo(Article target) {
        if(this.createDate.getTime() < target.getCreateDate().getTime()){
            return -1;
        }else if (this.createDate.getTime() > target.getCreateDate().getTime()){
            return 1;
        }
        return 0;
    }
}
