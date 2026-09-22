package com.example.music.controller.vo;

import com.example.music.constant.UserStatus;

/** 激活页用用户名换取 userId 和当前激活状态 */
public class ActivateStateVo extends BaseVo {
    private Integer userId;
    private UserStatus status;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public ActivateStateVo(int code, long cost, boolean success, String errorMassage) {
        super(code, cost, success, errorMassage);
    }

    public ActivateStateVo() {
    }
}