package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Repository
public class MusicRankRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void addMusic(int musicId){
        redisTemplate.opsForZSet().add(buildKey(),musicId,0);
    }
    public void deleteMusic(int musicId){
        redisTemplate.opsForZSet().remove(buildKey(),musicId);
    }
    public void modifyScore(int musicId,double delta){
        redisTemplate.opsForZSet().incrementScore(buildKey(),musicId,delta);
    }
    public List<Integer> getTopN(int n){
        Set<Object> objects = redisTemplate.opsForZSet().reverseRange(buildKey(), 0, n - 1);
        List<Integer> res = new ArrayList<>();
        for(Object o:objects){
            res.add((Integer) o);
        }
        return res;
    }
    private String buildKey(){
        return "music_rank";
    }
}
