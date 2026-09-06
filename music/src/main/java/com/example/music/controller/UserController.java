package com.example.music.controller;

import com.example.music.common.Result;
import com.example.music.controller.cmd.ModifyUserCmd;
import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.converter.UserVoConverter;
import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.MultiUserVo;
import com.example.music.controller.vo.SingleUserVo;
import com.example.music.controller.vo.UserVo;
import com.example.music.entity.User;
import com.example.music.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseVo register(@RequestBody RegisterCmd registerCmd) {
        long start = System.currentTimeMillis();
        long end;
        try {
            userService.register(registerCmd);

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

    @PostMapping("/login")
    public BaseVo login(String name, String password) {
        long start = System.currentTimeMillis();
        long end;

        try {
            userService.login(name, password);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);
            return baseVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            return baseVo;
        }
    }

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {
        long start = System.currentTimeMillis();
        long end;
        try {
            userService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @PutMapping("/modify")
    public BaseVo modify(@RequestBody ModifyUserCmd modifyUserCmd) {
        long start = System.currentTimeMillis();
        long end;
        try {
            userService.modify(modifyUserCmd);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/all")
    public MultiUserVo queryAll() {
        long start = System.currentTimeMillis();
        long end;
        MultiUserVo multiUserVo = new MultiUserVo();

        try {
            List<User> userList = userService.queryAll();
            List<UserVo> userVoList = UserVoConverter.convertToVoList(userList);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multiUserVo.setUserVoList(userVoList);
            multiUserVo.setBaseVo(baseVo);
            return multiUserVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multiUserVo.setBaseVo(baseVo);
            return multiUserVo;
        }
    }

    @GetMapping("/name")
    public MultiUserVo queryByName(String name) {
        long start = System.currentTimeMillis();
        long end;
        MultiUserVo multiUserVo = new MultiUserVo();

        try {
            List<User> userList = userService.queryByName(name);
            List<UserVo> userVoList = UserVoConverter.convertToVoList(userList);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multiUserVo.setUserVoList(userVoList);
            multiUserVo.setBaseVo(baseVo);
            return multiUserVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multiUserVo.setBaseVo(baseVo);
            return multiUserVo;
        }
    }

    @GetMapping("/id")
    public SingleUserVo queryById(int id) {
        long start = System.currentTimeMillis();
        long end;
        SingleUserVo singleUserVo = new SingleUserVo();

        try {
            User user = userService.queryById(id);

            if (user == null) {
                end = System.currentTimeMillis();
                BaseVo baseVo = new BaseVo(500, end - start, false, "用户不存在");
                singleUserVo.setBaseVo(baseVo);
                return singleUserVo;
            }

            UserVo userVo = UserVoConverter.convertToVo(user);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            singleUserVo.setUserVo(userVo);
            singleUserVo.setBaseVo(baseVo);
            return singleUserVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            singleUserVo.setBaseVo(baseVo);
            return singleUserVo;
        }
    }
    @PutMapping("/interest")
    public BaseVo selectInterest(int userId,String interests){
        long start = System.currentTimeMillis();
        long end;
        try {
            userService.selectInterests(userId,interests);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);
        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }
}
