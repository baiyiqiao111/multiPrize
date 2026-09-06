package com.example.music.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LikeRepository {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    public void userLike(int userId, int musicId ){
        redisTemplate.opsForList().rightPush(buildKey(userId),musicId);
    }
    public List<Integer> getUserLikeList(int userId){
        List<Integer> userLike = new ArrayList<>();
        List<Object> userRange = redisTemplate.opsForList().range(buildKey(userId), 0, -1);
        for(Object o:userRange){
            userLike.add((Integer) o);
        }
        return userLike;
    }
    public void musicLike(int userId, int musicId ){
        redisTemplate.opsForList().rightPush(buildMusicKey(musicId),userId);
    }
    public List<Integer> getMusicLikeList(int musicId){
        List<Integer> musicLike = new ArrayList<>();
        List<Object> musicRange = redisTemplate.opsForList().range(buildMusicKey(musicId), 0, -1);
        for(Object o:musicRange){
            musicLike.add((Integer) o);
        }
        return musicLike;
    }
    public void removeUserLike(int userId, int musicId){
        redisTemplate.opsForList().remove(buildKey(userId),1,musicId);
    }
    public void removeMusicLike(int userId, int musicId){
        redisTemplate.opsForList().remove(buildMusicKey(musicId),1,userId);
    }

    private String buildMusicKey(int musicId){
        return "music-like:"+musicId;
    }
    private String buildKey(int userId){
        return "user-like:"+userId;
    }
}
