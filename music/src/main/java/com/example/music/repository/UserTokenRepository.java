package com.example.music.repository;

import com.example.music.entity.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserTokenRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void add(int userId, UserToken userToken){
        redisTemplate.opsForValue().set(buildKey(userId),userToken,1, TimeUnit.MINUTES);
    }
    public UserToken get(int userId){
        return (UserToken) redisTemplate.opsForValue().get(buildKey(userId));
    }

    private String buildKey(int userId){
        return "user_token:"+userId;
    }
}