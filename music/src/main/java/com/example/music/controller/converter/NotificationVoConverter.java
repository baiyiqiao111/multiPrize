package com.example.music.controller.converter;

import com.example.music.controller.vo.NotificationVo;
import com.example.music.entity.Comment;
import com.example.music.entity.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationVoConverter {

    public static NotificationVo converterToVo(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationVo notificationVo = new NotificationVo();

        notificationVo.setId(notification.getId());
        notificationVo.setFromId(notification.getFromId());
        notificationVo.setToId(notification.getToId());
        notificationVo.setContentType(notification.getContentType());
        notificationVo.setContentId(notification.getContentId());
        notificationVo.setOperation(notification.getOperation());
        notificationVo.setOperationTime(notification.getOperationTime());

        return notificationVo;
    }

    public static List<NotificationVo> converterToVoList(List<Notification> notificationList) {
        List<NotificationVo> notificationVoList = new ArrayList<>();

        if (notificationList == null || notificationList.isEmpty()) {
            return notificationVoList;
        }

        for (Notification notification : notificationList) {
            notificationVoList.add(converterToVo(notification));
        }

        return notificationVoList;
    }
}