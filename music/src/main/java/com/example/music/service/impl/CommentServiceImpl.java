package com.example.music.service.impl;

import com.example.music.constant.DemoConstant;
import com.example.music.controller.cmd.AddCommentCmd;
import com.example.music.controller.cmd.ModifyCommentCmd;
import com.example.music.entity.Comment;
import com.example.music.entity.Notification;
import com.example.music.exception.CommentNotExistException;
import com.example.music.mapper.CommentMapper;
import com.example.music.repository.MusicRankRepository;
import com.example.music.service.CommentService;
import com.example.music.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private  CommentMapper commentMapper;
    @Autowired
    private MusicRankRepository musicRankRepository;
    @Autowired
    private NotificationService notificationService;
    @Override
    public List<Comment> queryByMusicId(int musicId) {
        return commentMapper.queryByMusicId(musicId);
    }

    @Override
    public void add(AddCommentCmd addCommentCmd){
        Comment comment = new Comment();

        comment.setUserId(addCommentCmd.getUserId());
        comment.setMusicId(addCommentCmd.getMusicId());
        comment.setParentId(addCommentCmd.getParentId());
        comment.setComment(addCommentCmd.getComment());

        commentMapper.add(comment);
        musicRankRepository.modifyScore(addCommentCmd.getMusicId(), DemoConstant.COMMENT_SCORE);
        if(addCommentCmd.getParentId()==0){
            return;
        }
        Comment parentComment = commentMapper.queryById(addCommentCmd.getParentId());
        Notification notification = new Notification();
        notification.setFromId(addCommentCmd.getUserId());
        notification.setToId(parentComment.getUserId());
        notification.setContentType("COMMENT");
        notification.setContentId(parentComment.getId());
        notification.setOperation("APPLY");
        notificationService.add(notification);
    }

    @Override
    public void modify(String comment, int id) {
        Comment commentInDb = commentMapper.queryById(id);
        if(commentInDb==null){
            throw new CommentNotExistException("评论不存在");
        }
        commentInDb.setComment(comment);
        commentMapper.modify(commentInDb);
    }

    @Override
    public void delete(int id) {
        int count = 0;
        Queue<Integer> q = new LinkedList<>();
        Comment comment = commentMapper.queryById(id);
        q.offer(id);
        while(!q.isEmpty()){
            int poll = q.poll();
            commentMapper.delete(poll);
            count++;
            List<Comment> comments = commentMapper.queryByParentId(poll);
            for(int i=0;i<comments.size();i++){
                q.offer(comments.get(i).getId());
            }
        }
        musicRankRepository.modifyScore(comment.getMusicId(), DemoConstant.COMMENT_SCORE);
    }

    @Override
    public Comment queryById(int id) {
        return commentMapper.queryById(id);
    }

    @Override
    public List<Comment> queryByParentId(int parentId) {
        return commentMapper.queryByParentId(parentId);
    }
}
