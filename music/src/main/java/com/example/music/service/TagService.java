package com.example.music.service;

import com.example.music.entity.Music;

public interface TagService {
    /**
     * 根据音乐的标签，向订阅了这些标签的用户发送上新推荐邮件，
     * 邮件中包含一个可跳转到该音频详情页的链接。
     */
    void recommend(Music music);

}
