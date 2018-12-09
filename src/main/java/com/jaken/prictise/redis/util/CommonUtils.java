package com.jaken.prictise.redis.util;

import com.jaken.prictise.redis.entity.Article;
import com.jaken.prictise.redis.entity.RedisMapper;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonUtils {

    public static String genUUID(){
        return UUID.randomUUID().toString();
    }

    public static <T> Map<String,String>  redisConvert(T t){
        try {
            Map<String,String> redisMapper = new HashMap<String,String>();
            Class classT = t.getClass();
            Field[] fields = classT.getDeclaredFields();
            for(Field field : fields){
                if(checkDeclaredAnnotation(field.getDeclaredAnnotations(),RedisMapper.class)){
                    field.setAccessible(true);
                    String fieldStringValue = convertToString(field.get(t));
                    redisMapper.put(field.getName(),fieldStringValue);
                }
            }
            return redisMapper;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T redisConvert(T t,Map<String,String> redisMapper){
        try {
            Class classT = t.getClass();
            Field[] fields = classT.getDeclaredFields();
            for(Field field : fields){
                if(checkDeclaredAnnotation(field.getDeclaredAnnotations(),RedisMapper.class)
                        && redisMapper.get(field.getName()) != null){
                    field.setAccessible(true);
                    Object fieldValue = convertToFieldType(field.getType(),redisMapper.get(field.getName()));
                    field.set(t,fieldValue);
                }
            }
            return t;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkDeclaredAnnotation(Annotation[] annotations,Class targetAnno){
        for(Annotation annotation:annotations){
            if(annotation.annotationType() == targetAnno){
                return true;
            }
        }
        return false;
    }

    private static <T> String convertToString(T t){
        if(t.getClass() == String.class){
            return (String)t;
        }else if(t.getClass() == Integer.class){
            return String.valueOf(t);
        }else if(t.getClass() == Long.class){
            return String.valueOf(t);
        }else if(t.getClass() == Float.class){
            return String.valueOf(t);
        }else if(t.getClass() == Double.class){
            return String.valueOf(t);
        }else if(t.getClass() == Date.class){
            Date targetDate = (Date)t;
            return String.valueOf(targetDate.getTime());
        }else{
            return t.toString();
        }
    }

    private static <T> T convertToFieldType(Class<T> t,String target){
        if(t == String.class){
            return (T) target;
        }else if(t == Integer.class){
            return (T)new Integer(Integer.parseInt(target));
        }else if(t == Long.class){
            return (T)new Long(Long.parseLong(target));
        }else if(t == Float.class){
            return (T)new Float(Float.parseFloat(target));
        }else if(t == Double.class){
            return (T)new Double(Double.parseDouble(target));
        }else if(t == Date.class){
            Long targetTimeMills = Long.parseLong(target);
            Date targetDate = new Date();
            targetDate.setTime(targetTimeMills);
            return (T)targetDate;
        }else{
            return (T)target;
        }
    }

    public static void main(String[] args) {
        Article article = new Article(CommonUtils.genUUID(),"我的测试","新的一天开始了，这是一个无聊的文章内容");
        System.out.println(redisConvert(article));
    }
}
