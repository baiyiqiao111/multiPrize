package com.example.music.service;

import com.example.music.controller.cmd.AddCommentCmd;
import com.example.music.controller.cmd.AddMusicCmd;
import com.example.music.controller.cmd.ModifyCommentCmd;
import com.example.music.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> queryByMusicId(int musicId);

    void add(AddCommentCmd addCommentCmd);

    void modify(String comment, int id);

    void delete(int id);

    Comment queryById(int id);

    List<Comment> queryByParentId(int parentId);
}
