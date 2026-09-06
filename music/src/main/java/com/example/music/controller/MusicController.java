package com.example.music.controller;

import com.example.music.controller.cmd.AddMusicCmd;
import com.example.music.controller.cmd.ModifyMusicCmd;
import com.example.music.controller.converter.MusicVoConverter;
import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.MultiMusicVo;
import com.example.music.controller.vo.MusicVo;
import com.example.music.controller.vo.SingleMusicVo;
import com.example.music.entity.Music;
import com.example.music.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/music")
@Slf4j
public class MusicController {
    @Autowired
    private MusicService musicService;

    @PostMapping("/add")
    public BaseVo add(@RequestBody AddMusicCmd addMusicCmd) {
        long start = System.currentTimeMillis();
        long end;

        try {
            musicService.add(addMusicCmd);

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

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {
        long start = System.currentTimeMillis();
        long end;

        try {
            musicService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @PutMapping("/modify")
    public BaseVo modify(@RequestBody ModifyMusicCmd modifyMusicCmd) {
        long start = System.currentTimeMillis();
        long end;

        try {
            musicService.modify(modifyMusicCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/all")
    public MultiMusicVo queryAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        long start = System.currentTimeMillis();
        long end;
        MultiMusicVo multiMusicVo = new MultiMusicVo();

        try {

            List<Music> musicList = musicService.queryAll(pageNum, pageSize);
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

    @GetMapping("/title")
    public SingleMusicVo queryByTitle(String title) {
        long start = System.currentTimeMillis();
        long end;
        SingleMusicVo singleMusicVo = new SingleMusicVo();

        try {
            Music music = musicService.queryByTitle(title);

            if (music == null) {
                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(500, end - start, false, "音乐不存在");
                singleMusicVo.setBaseVo(baseVo);
                return singleMusicVo;
            }

            MusicVo musicVo = MusicVoConverter.convertToVo(music);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            singleMusicVo.setMusicVo(musicVo);
            singleMusicVo.setBaseVo(baseVo);

            return singleMusicVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singleMusicVo.setBaseVo(baseVo);
            return singleMusicVo;
        }
    }

    @GetMapping("/id")
    public SingleMusicVo queryById(int id) {
        long start = System.currentTimeMillis();
        long end;
        SingleMusicVo singleMusicVo = new SingleMusicVo();

        try {
            Music music = musicService.queryById(id);

            if (music == null) {
                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(500, end - start, false, "音乐不存在");
                singleMusicVo.setBaseVo(baseVo);
                return singleMusicVo;
            }

            MusicVo musicVo = MusicVoConverter.convertToVo(music);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            singleMusicVo.setMusicVo(musicVo);
            singleMusicVo.setBaseVo(baseVo);

            return singleMusicVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singleMusicVo.setBaseVo(baseVo);
            return singleMusicVo;
        }
    }
}
