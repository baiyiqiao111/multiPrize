package com.example.music.controller.converter;

import com.example.music.controller.vo.MusicVo;
import com.example.music.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicVoConverter {
    public static MusicVo convertToVo(Music music) {
        if (music == null) {
            return null;
        }

        MusicVo musicVo = new MusicVo();

        musicVo.setId(music.getId());
        musicVo.setTitle(music.getTitle());
        musicVo.setContent(music.getContent());
        musicVo.setTags(music.getTags());
        musicVo.setPublishTime(music.getPublishTime());
        musicVo.setPictureUrl(music.getPictureUrl());
        musicVo.setSoundUrl(music.getSoundUrl());
        musicVo.setAuthor(music.getAuthor());

        return musicVo;
    }

    public static List<MusicVo> convertToVoList(List<Music> musicList) {
        List<MusicVo> musicVoList = new ArrayList<>();

        if (musicList == null) {
            return musicVoList;
        }

        for (Music music : musicList) {
            musicVoList.add(convertToVo(music));
        }

        return musicVoList;
    }
}
