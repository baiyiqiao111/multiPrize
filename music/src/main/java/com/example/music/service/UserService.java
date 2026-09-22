package com.example.music.service;

import com.example.music.controller.cmd.ModifyUserCmd;
import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.entity.User;

import java.util.List;

public interface UserService {

    /** 注册：用户名不能重复，账号状态为 INIT，返回新用户的 id 供前端跳转激活页 */
    int register(RegisterCmd registerCmd);

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

    /** 激活：校验邮件激活码，通过后把状态从 INIT 改成 ACTIVE */
    void activate(int userId, String inputCode);

    /** 重新给用户邮箱发一封激活码邮件（激活码 10 分钟过期） */
    void resendActivateCode(int userId);

    /** 根据用户名精确查询，前端激活页用用户名换 id */
    User queryByExactName(String name);
}
