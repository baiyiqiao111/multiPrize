package com.example.music.controller;

import com.example.music.controller.converter.MusicVoConverter;
import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.MultiMusicVo;
import com.example.music.controller.vo.MusicVo;
import com.example.music.entity.Music;
import com.example.music.service.MusicRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/music-rank")
public class MusicRankController {
    @Autowired
    private MusicRankService musicRankService;
    @GetMapping("/topN")
    public MultiMusicVo getTopN(int n){
        long start = System.currentTimeMillis();
        long end;
        MultiMusicVo multiMusicVo = new MultiMusicVo();

        try {

            List<Music> musicList = musicRankService.getTopN(n);
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
}
