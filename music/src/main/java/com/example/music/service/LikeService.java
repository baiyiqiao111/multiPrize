package com.example.music.service;

import com.example.music.entity.Music;
import com.example.music.entity.User;

import java.util.List;

public interface LikeService {
    void like(int userId, int musicId);
    void delete(int userId, int musicId);
    List<Music> queryUserLikeList(int userId);
    List<User> queryMusicLikeList(int musicId);
}
