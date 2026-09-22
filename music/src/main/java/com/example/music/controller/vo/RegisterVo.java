package com.example.music.controller.vo;

/** 注册结果，多带一个 userId，前端拿它跳转到激活页 */
public class RegisterVo extends BaseVo {
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public RegisterVo(int code, long cost, boolean success, String errorMassage) {
        super(code, cost, success, errorMassage);
    }

    public RegisterVo() {
    }
}