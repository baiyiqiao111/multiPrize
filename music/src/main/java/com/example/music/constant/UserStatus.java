package com.example.music.constant;

public enum UserStatus {
    /** 注册完成，还没有用邮件里的激活码激活 */
    INIT,
    /** 激活码校验通过，账号可正常使用 */
    ACTIVE
}