package com.example.music.service.impl;

import com.example.music.entity.Music;
import com.example.music.mapper.MusicMapper;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.MusicRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MusicRankServiceImpl implements MusicRankService {
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private MusicMapper musicMapper;
    @Override
    public List<Music> getTopN(int n) {
        List<Integer> topNMusicIds = musicRankRepository.getTopN(n);
        List<Music> musicTopN = musicMapper.queryByIds(topNMusicIds);
        return musicTopN;
    }
}
