package com.example.music.service;

import com.example.music.entity.Notification;

import java.util.List;

public interface NotificationService{
    void add(Notification notification);
    void delete(int id);
    List<Notification> queryAll(int toId,int start, int size);
}
