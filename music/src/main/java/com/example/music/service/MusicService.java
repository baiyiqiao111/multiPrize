package com.example.music.service;

import com.example.music.controller.cmd.AddMusicCmd;
import com.example.music.controller.cmd.ModifyMusicCmd;
import com.example.music.entity.Music;

import java.util.List;
public interface MusicService {
    void add(AddMusicCmd addMusicCmd);

    void modify(ModifyMusicCmd modifyMusicCmd);

    void delete(Integer id);

    Music queryById(Integer id);

    Music queryByTitle(String title);

    List<Music> queryAll(Integer pageNum, Integer pageSize);

    List<Music> queryByIds(List<Integer> idList);
}
