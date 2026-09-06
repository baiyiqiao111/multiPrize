package com.example.music.controller.vo;

public class SingleCommentVo {
    private CommentVo commentVo;
    private BaseVo baseVo;

    public CommentVo getCommentVo() {
        return commentVo;
    }

    public void setCommentVo(CommentVo commentVo) {
        this.commentVo = commentVo;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public SingleCommentVo(CommentVo commentVo, BaseVo baseVo) {
        this.commentVo = commentVo;
        this.baseVo = baseVo;
    }

    public SingleCommentVo() {
    }
}
