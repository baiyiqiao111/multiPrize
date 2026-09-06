package com.example.music.controller.vo;

import java.util.List;

public class MultiCommentVo {
    private List<CommentVo> commentVoList;
    private BaseVo baseVo;

    public List<CommentVo> getCommentVoList() {
        return commentVoList;
    }

    public void setCommentVoList(List<CommentVo> commentVoList) {
        this.commentVoList = commentVoList;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public MultiCommentVo(List<CommentVo> commentVoList, BaseVo baseVo) {
        this.commentVoList = commentVoList;
        this.baseVo = baseVo;
    }

    public MultiCommentVo() {
    }
}
