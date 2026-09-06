package com.example.music.service.impl;

import com.example.music.controller.cmd.AddMusicCmd;
import com.example.music.controller.cmd.ModifyMusicCmd;
import com.example.music.entity.Comment;
import com.example.music.entity.Music;
import com.example.music.mapper.CommentMapper;
import com.example.music.mapper.MusicMapper;
import com.example.music.producer.AddMusicProducer;
import com.example.music.producer.AddUserProducer;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.MusicService;
import com.example.music.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private AddMusicProducer addMusicProducer;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Override
    public void add(AddMusicCmd addMusicCmd) {
        Music exist = musicMapper.queryByTitle(addMusicCmd.getTitle());

        if (exist != null) {
            throw new IllegalArgumentException("音乐已存在：" + addMusicCmd.getTitle());
        }
        Music music = new Music();
        music.setAuthor(addMusicCmd.getAuthor());
        music.setContent(addMusicCmd.getContent());
        music.setTags(addMusicCmd.getTags());
        music.setTitle(addMusicCmd.getTitle());
        music.setPictureUrl(addMusicCmd.getPictureUrl());
        music.setSoundUrl(addMusicCmd.getSoundUrl());
        musicMapper.add(music);
        Music musicInDb = musicMapper.queryByTitle(music.getTitle());
        //todo:放入消息队列
        musicRankRepository.addMusic(music.getId());
        addMusicProducer.send("addMusic_topic",musicInDb.getId());
    }

    @Override
    public void modify(ModifyMusicCmd modifyMusicCmd) {
        Music exist = musicMapper.queryById(modifyMusicCmd.getId());

        if (exist == null) {
            throw new IllegalArgumentException("音乐不存在");
        }
        exist.setAuthor(modifyMusicCmd.getAuthor());
        exist.setContent(modifyMusicCmd.getContent());
        exist.setTags(modifyMusicCmd.getTags());
        exist.setTitle(modifyMusicCmd.getTitle());
        exist.setPictureUrl(modifyMusicCmd.getPictureUrl());
        exist.setSoundUrl(modifyMusicCmd.getSoundUrl());
        exist.setId(modifyMusicCmd.getId());
        musicMapper.modify(exist);
    }

    @Override
    public void delete(Integer id) {
        Music exist = musicMapper.queryById(id);

        if (exist == null) {
            throw new IllegalArgumentException("音乐不存在");
        }

        musicMapper.delete(id);
        commentMapper.deleteByMusicId(id);
        musicRankRepository.deleteMusic(id);
    }

    @Override
    public Music queryById(Integer id) {
        return musicMapper.queryById(id);
    }

    @Override
    public Music queryByTitle(String title) {
        return musicMapper.queryByTitle(title);
    }

    @Override
    public List<Music> queryAll(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        return musicMapper.queryAll(offset, pageSize);
    }

    @Override
    public List<Music> queryByIds(List<Integer> idList) {
        return musicMapper.queryByIds(idList);
    }
}
