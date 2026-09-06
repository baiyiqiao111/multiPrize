package com.example.music.controller;

import com.example.music.controller.cmd.AddCommentCmd;
import com.example.music.controller.cmd.ModifyCommentCmd;
import com.example.music.controller.converter.CommentVoConverter;
import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.CommentVo;
import com.example.music.controller.vo.MultiCommentVo;
import com.example.music.controller.vo.SingleCommentVo;
import com.example.music.entity.Comment;
import com.example.music.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public BaseVo add(@RequestBody AddCommentCmd addCommentCmd) {
        long start = System.currentTimeMillis();
        long end;

        try {
            commentService.add(addCommentCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, e.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }
    @PutMapping("/modify")
    public BaseVo modify(String comment, int id) {
        long start = System.currentTimeMillis();
        long end;
        try {
            commentService.modify(comment,id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }
        @GetMapping("/by-music")
        public MultiCommentVo queryByMusicId(int musicId) {
            long start = System.currentTimeMillis();
            long end;
            MultiCommentVo multiCommentVo = new MultiCommentVo();

            try {
                List<Comment> commentList = commentService.queryByMusicId(musicId);
                List<CommentVo> commentVoList = CommentVoConverter.convertToVoList(commentList);

                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(200, end - start, true, null);

                multiCommentVo.setCommentVoList(commentVoList);
                multiCommentVo.setBaseVo(baseVo);

                return multiCommentVo;

            } catch (Exception e) {
                log.error(e.getMessage());
                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
                multiCommentVo.setBaseVo(baseVo);
                return multiCommentVo;
            }
        }
        @DeleteMapping("/delete")
        public BaseVo delete(int id){
            long start = System.currentTimeMillis();
            long end;

            try {
                commentService.delete(id);
                end = System.currentTimeMillis();
                return new BaseVo(200, end - start, true, null);

            } catch (Exception e) {
                log.error(e.getMessage());
                end = System.currentTimeMillis();
                return new BaseVo(500, end - start, false, "其它位置异常");
            }

        }

        @GetMapping("/id")
        public SingleCommentVo queryById(int id){
            long start = System.currentTimeMillis();
            long end;
            SingleCommentVo singleCommentVo = new SingleCommentVo();

            try {
                Comment comment = commentService.queryById(id);

                if (comment == null) {
                    end = System.currentTimeMillis();
                    BaseVo baseVo = new BaseVo(500, end - start, false, "评论不存在");
                    singleCommentVo.setBaseVo(baseVo);
                    return singleCommentVo;
                }

                CommentVo commentVo = CommentVoConverter.convertToVo(comment);

                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(200, end - start, true, null);

                singleCommentVo.setCommentVo(commentVo);
                singleCommentVo.setBaseVo(baseVo);

                return singleCommentVo;

            } catch (Exception e) {
                log.error(e.getMessage());
                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
                singleCommentVo.setBaseVo(baseVo);
                return singleCommentVo;
            }
        }
    }
