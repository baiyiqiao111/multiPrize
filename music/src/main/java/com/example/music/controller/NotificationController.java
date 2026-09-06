package com.example.music.controller;

import com.example.music.controller.converter.NotificationVoConverter;
import com.example.music.controller.vo.BaseVo;
import com.example.music.controller.vo.MultiNotificationVo;
import com.example.music.controller.vo.NotificationVo;
import com.example.music.entity.Notification;
import com.example.music.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @DeleteMapping("/delete")
    public BaseVo delete(int id) {
        long start = System.currentTimeMillis();
        long end;

        try {
            notificationService.delete(id);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

    @GetMapping("/all")
    public MultiNotificationVo queryAll(@RequestParam int toId,
                                        @RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        long start = System.currentTimeMillis();
        long end;
        MultiNotificationVo multiNotificationVo = new MultiNotificationVo();

        try {
            int startIndex = (pageNum - 1) * pageSize;

            List<Notification> notificationList =
                    notificationService.queryAll(toId, startIndex, pageSize);
            List<NotificationVo> notificationVos = NotificationVoConverter.converterToVoList(notificationList);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            multiNotificationVo.setNotificationVoList(notificationVos);
            multiNotificationVo.setBaseVo(baseVo);

            return multiNotificationVo;

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常");
            multiNotificationVo.setBaseVo(baseVo);
            return multiNotificationVo;
        }
    }
}