package com.example.music.mapper;

import com.example.music.entity.Music;
import com.example.music.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface NotificationMapper {
    void add(Notification notification);
    void delete(int id);
    List<Notification> queryAll(int toId,int start, int size);
}
