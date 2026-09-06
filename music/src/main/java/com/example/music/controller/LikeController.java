package com.example.music.controller;

import com.example.music.controller.converter.MusicVoConverter;
import com.example.music.controller.converter.UserVoConverter;
import com.example.music.controller.vo.*;
import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@Slf4j
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    public BaseVo like(int userId, int musicId) {
        long start = System.currentTimeMillis();
        long end;

        try {
            likeService.like(userId, musicId);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @DeleteMapping("/delete")
    public BaseVo delete(int userId, int musicId) {
        long start = System.currentTimeMillis();
        long end;

        try {
            likeService.delete(userId, musicId);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/user")
    public MultiMusicVo queryUserLikeList(int userId) {
        long start = System.currentTimeMillis();
        long end;
        MultiMusicVo multiMusicVo = new MultiMusicVo();

        try {
            List<Music> musicList = likeService.queryUserLikeList(userId);
            List<MusicVo> musicVoList = MusicVoConverter.convertToVoList(musicList);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multiMusicVo.setMusicVoList(musicVoList);
            multiMusicVo.setBaseVo(baseVo);

            return multiMusicVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multiMusicVo.setBaseVo(baseVo);
            return multiMusicVo;
        }
    }

    @GetMapping("/music")
    public MultiUserVo queryMusicLikeList(int musicId) {
        long start = System.currentTimeMillis();
        long end;
        MultiUserVo multipleUserVo = new MultiUserVo();

        try {
            List<User> userList = likeService.queryMusicLikeList(musicId);
            List<UserVo> userVoList = UserVoConverter.convertToVoList(userList);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multipleUserVo.setUserVoList(userVoList);
            multipleUserVo.setBaseVo(baseVo);

            return multipleUserVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multipleUserVo.setBaseVo(baseVo);
            return multipleUserVo;
        }
    }
}
