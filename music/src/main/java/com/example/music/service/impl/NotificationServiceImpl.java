package com.example.music.service.impl;

import com.example.music.entity.Notification;
import com.example.music.mapper.NotificationMapper;
import com.example.music.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void add(Notification notification) {
        notificationMapper.add(notification);
    }

    @Override
    public void delete(int id) {
        notificationMapper.delete(id);
    }

    @Override
    public List<Notification> queryAll(int toId, int start, int size) {
        return notificationMapper.queryAll(toId,start,size);
    }
}
