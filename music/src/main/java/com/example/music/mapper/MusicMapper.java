package com.example.music.mapper;

import com.example.music.entity.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {
    Music queryByTitle(String title);

    List<Music> queryAll(int start, int size);

    void add(Music music);

    void modify(Music music);

    void delete(int id);

    Music queryById(int id);

    List<Music> queryByIds(List<Integer> idList);
}
