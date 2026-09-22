package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ActiveCodeRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void set(int userId, String code){
        redisTemplate.opsForValue().set(buildKey(userId),code,10, TimeUnit.MINUTES);
    }

    /** 激活码不存在或已过期时返回 null，由上层给出提示 */
    public String get(int userId){
        Object code = redisTemplate.opsForValue().get(buildKey(userId));
        return code == null ? null : code.toString();
    }

    /** 激活成功后删除，避免同一个激活码被重复使用 */
    public void delete(int userId){
        redisTemplate.delete(buildKey(userId));
    }
    private String buildKey(int userId){
        return "active_code:"+userId;
    }
}
