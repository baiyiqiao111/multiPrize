package com.example.music.service.impl;

import com.example.music.constant.DemoConstant;
import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.mapper.MusicMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.repository.LikeRepository;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public void like(int userId, int musicId) {
        likeRepository.userLike(userId,musicId);
        likeRepository.musicLike(userId,musicId);
        musicRankRepository.modifyScore(musicId, DemoConstant.LIKE_SCORE);
    }

    @Override
    public void delete(int userId, int musicId) {
        likeRepository.removeUserLike(userId,musicId);
        likeRepository.removeMusicLike(userId,musicId);
        musicRankRepository.modifyScore(musicId, -DemoConstant.LIKE_SCORE);
    }

    @Override
    public List<Music> queryUserLikeList(int userId) {
        List<Integer> userLikeList = likeRepository.getUserLikeList(userId);
        if(userLikeList==null||userLikeList.size()==0){
            return new ArrayList<>();
        }
        return musicMapper.queryByIds(userLikeList);
    }

    @Override
    public List<User> queryMusicLikeList(int musicId) {
        List<Integer> musicLikeList = likeRepository.getMusicLikeList(musicId);
        if(musicLikeList==null||musicLikeList.size()==0){
            return new ArrayList<>();
        }
        return userMapper.queryByIds(new HashSet<>(musicLikeList));
    }
}
