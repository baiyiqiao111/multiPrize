package com.example.music.controller.vo;

import com.example.music.entity.Notification;

import java.util.List;

public class MultiNotificationVo {
    private List<NotificationVo> notificationVoList;
    private BaseVo baseVo;

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public List<NotificationVo> getNotificationVoList() {
        return notificationVoList;
    }

    public void setNotificationVoList(List<NotificationVo> notificationVoList) {
        this.notificationVoList = notificationVoList;
    }
}
