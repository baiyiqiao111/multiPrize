package com.example.music.controller.cmd;

import com.example.music.entity.Comment;

public class ModifyCommentCmd {
    private int id;
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ModifyCommentCmd(int id, String comment) {
        this.id = id;
        this.comment = comment;
    }

    public ModifyCommentCmd() {
    }
}
