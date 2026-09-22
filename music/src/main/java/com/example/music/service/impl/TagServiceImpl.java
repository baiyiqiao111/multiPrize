package com.example.music.service.impl;

import com.example.music.entity.Music;
import com.example.music.entity.User;
import com.example.music.integration.EmailUtil;
import com.example.music.mapper.UserMapper;
import com.example.music.repository.TagRepository;
import com.example.music.service.TagService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailUtil emailUtil;

    /** 站点访问地址，用于拼接详情页链接；可在 application.properties 中通过 app.base-url 覆盖 */
    @Value("${app.base-url:http://107.22.136.67:8080}")
    private String baseUrl;

    @Override
    public void recommend(Music music) {
        log.info("开始进入推荐逻辑");
        if (music == null || music.getTags() == null) {
            return;
        }
        //把tags转化成tagArray
        String[] tagArray = music.getTags().split(",");
        Set<Integer> allUsersIds = new HashSet<>();
        for (String tag : tagArray) {
            String trimmed = tag.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Set<Integer> userIds = tagRepository.get(trimmed);
            allUsersIds.addAll(userIds);
        }
        if (allUsersIds.isEmpty()) {
            return;
        }
        //遍历Array拿到用户列表
        List<User> users = userMapper.queryByIds(allUsersIds);

        //当前音频详情页链接
        String detailUrl = baseUrl + "/detail.html?id=" + music.getId();
        String subject = "上新推荐：" + safe(music.getTitle());

        //根据列表发邮件
        for (User user : users) {
            String content = buildContent(user, music, detailUrl);
            try {
                emailUtil.sendMile(user.getEmail(), subject, content);
            } catch (MessagingException e) {
                //单个用户发送失败不影响其他用户
                log.error("发送上新推荐邮件失败，email={}", user.getEmail(), e);
            }
        }
    }

    /** 构建 HTML 邮件正文，包含一个可跳转到当前音频详情页的链接 */
    private String buildContent(User user, Music music, String detailUrl) {
        String greeting = (user.getName() == null || user.getName().isEmpty())
                ? "你好" : safe(user.getName());
        String cover = (music.getPictureUrl() == null || music.getPictureUrl().isEmpty())
                ? ""
                : "<img src=\"" + safe(music.getPictureUrl()) + "\" alt=\"封面\""
                    + " style=\"width:160px;height:160px;object-fit:cover;border-radius:8px;\"/>";
        String intro = (music.getContent() == null || music.getContent().isEmpty())
                ? ""
                : "<p style=\"color:#46546b;font-size:14px;line-height:1.7;margin:12px 0;\">"
                    + safe(music.getContent()) + "</p>";

        return "<div style=\"max-width:600px;margin:0 auto;font-family:'Microsoft YaHei',sans-serif;"
                + "background:#f4f6f9;padding:24px;color:#2c3e50;\">"
                + "<div style=\"background:#fff;border-radius:10px;padding:24px;"
                + "box-shadow:0 1px 4px rgba(0,0,0,.06);\">"
                + "<p style=\"font-size:16px;\">" + greeting + "，有新内容上线啦！</p>"
                + "<p style=\"font-size:14px;color:#7a8699;\">根据你关注的标签，为你推荐：</p>"
                + "<div style=\"display:flex;gap:16px;margin:16px 0;\">"
                + cover
                + "<div>"
                + "<h2 style=\"font-size:20px;margin:0 0 6px;\">" + safe(music.getTitle()) + "</h2>"
                + "<p style=\"font-size:14px;color:#7a8699;margin:0;\">"
                + (music.getAuthor() == null || music.getAuthor().isEmpty() ? "未知歌手" : safe(music.getAuthor()))
                + "</p>"
                + "<p style=\"font-size:12px;color:#aab;margin:6px 0;\">标签："
                + safe(music.getTags()) + "</p>"
                + "</div>"
                + "</div>"
                + intro
                + "<p style=\"text-align:center;margin:24px 0 8px;\">"
                + "<a href=\"" + detailUrl + "\" "
                + "style=\"display:inline-block;background:#4a90d9;color:#fff;text-decoration:none;"
                + "font-size:15px;padding:12px 28px;border-radius:6px;\">立即收听 &gt;</a>"
                + "</p>"
                + "<p style=\"text-align:center;font-size:12px;color:#aab;\">"
                + "如果按钮无法点击，请复制以下链接到浏览器打开：<br/>"
                + "<a href=\"" + detailUrl + "\" style=\"color:#4a90d9;\">" + detailUrl + "</a></p>"
                + "</div></div>";
    }

    /** 简单的 HTML 转义，避免标题/简介中的特殊字符破坏邮件结构 */
    private String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}