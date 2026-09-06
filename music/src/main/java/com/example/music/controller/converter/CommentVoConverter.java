package com.example.music.controller.converter;

import com.example.music.controller.vo.CommentVo;
import com.example.music.entity.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentVoConverter {
    public static CommentVo convertToVo(Comment comment) {

        if (comment == null) {
            return null;
        }

        CommentVo commentVo = new CommentVo();

        commentVo.setId(comment.getId());
        commentVo.setUserId(comment.getUserId());
        commentVo.setMusicId(comment.getMusicId());
        commentVo.setParentId(comment.getParentId());
        commentVo.setComment(comment.getComment());
        commentVo.setPublishTime(comment.getPublishTime());

        return commentVo;
    }

    public static List<CommentVo> convertToVoList(List<Comment> commentList) {

        List<CommentVo> commentVoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentVoList.add(convertToVo(comment));
        }

        return commentVoList;
    }
}
