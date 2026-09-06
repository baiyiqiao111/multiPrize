package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class TagRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void add(String tag,int userId){
        redisTemplate.opsForSet().add(buildKey(tag),userId);
    }
    public void delete(String tag,int userId){
        redisTemplate.opsForSet().remove(buildKey(tag),userId);
    }

    public Set<Integer> get(String tag){
        Set<Integer> res = new HashSet<>();
        Set<Object> members = redisTemplate.opsForSet().members(buildKey(tag));
        for(Object o : members){
            res.add((Integer) o);
        }
        return res;
    }

    private String buildKey(String tag){
        return "recommend_tag:"+tag;
    }
}
