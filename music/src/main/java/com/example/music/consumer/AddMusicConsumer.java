package com.example.music.consumer;

import com.example.music.entity.Music;
import com.example.music.mapper.MusicMapper;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class AddMusicConsumer {
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @KafkaListener(topics = "addMusic_topic",groupId = "music-group")
    public void listen(String message){
        int id = Integer.parseInt(message);
        Music music = musicMapper.queryById(id);
        tagService.recommend(music);
    }

}
