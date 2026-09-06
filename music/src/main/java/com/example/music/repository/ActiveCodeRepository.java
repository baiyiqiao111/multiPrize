package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
public class ActiveCodeRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void set(int userId, String code){
        redisTemplate.opsForValue().set(buildKey(userId),code,10, TimeUnit.MINUTES);
    }
    public String get(int userId){
        return Objects.requireNonNull(redisTemplate.opsForValue().get(buildKey(userId))).toString();
    }
    private String buildKey(int userId){
        return "active_code:"+userId;
    }
}
