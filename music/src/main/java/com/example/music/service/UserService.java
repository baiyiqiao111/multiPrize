package com.example.music.service;

import com.example.music.controller.cmd.ModifyUserCmd;
import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.entity.User;

import java.util.List;

public interface UserService {

    /** 注册：用户名不能重复，成功返回带 id 的用户 */
    void register(RegisterCmd registerCmd);

    /** 登录：用户名 + 密码校验，成功返回用户，失败返回 null */
    void login(String name, String password);

    /** 根据 id 删除 */
    void delete(int id);

    /** 修改用户信息 */
    void modify(ModifyUserCmd modifyUserCmd);

    /** 查询所有 */
    List<User> queryAll();

    /** 根据用户名模糊查询 */
    List<User> queryByName(String name);

    /** 根据 id 查询 */
    User queryById(int id);

    void selectInterests(int userId,String interests);
}
