package com.example.music.mapper;

import com.example.music.controller.cmd.ModifyCommentCmd;
import com.example.music.entity.Comment;
import com.example.music.entity.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> queryByMusicId(int musicId);

    void add(Comment comment);

    void modify(Comment comment);

    void delete(int id);

    Comment queryById(int id);

    List<Comment> queryByParentId(int parentId);

    void deleteByMusicId(int musicId);
}
