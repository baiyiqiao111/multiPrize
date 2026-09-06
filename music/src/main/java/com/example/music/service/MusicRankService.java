package com.example.music.service;

import com.example.music.entity.Music;

import java.util.List;

public interface MusicRankService {
    List<Music> getTopN(int n);
}
