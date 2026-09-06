package com.example.AI_chatbot.context;

import com.example.AI_chatbot.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class McpProfessionalContextManager {
    @Value("${openai.max-message-size}")
    private Integer maxMessageSize;
    private final Map<String, List<ChatMessage>> sessionContextMap = new ConcurrentHashMap<>();


    public List<ChatMessage> getContext(String sessionId){
        List<ChatMessage> chatMessageList= sessionContextMap.get(sessionId);
        if(chatMessageList==null){
            chatMessageList = new ArrayList<>();
            //模型自己不知道"今天"是几号，必须把服务器当前时间告诉它，否则"查今天"会算错日期
            LocalDateTime now = LocalDateTime.now();
            String systemPrompt = """
                你是业务查询参数抽取器，只输出JSON，禁止多余文字、禁止markdown、禁止代码块围栏。

                当前服务器时间是：%s（今天是 %s）。所有相对时间（今天、昨天、最近7天等）都以这个时间为准换算。

                可选意图：

                PLAY_RECORD：查询播放记录

                PRIZE_RECORD：查询发奖记录

                CHAT：普通闲聊，不需要查询业务



                抽取规则：

                1. targetUserId：直接设置null

                2. startTime、endTime：用户提到时间段就提取，格式yyyy-MM-dd HH:mm:ss；没有提供默认取最近30天 ,用户如果指定当天则取当天的0点到当天的23点59分59秒

                3. startIndex、pageSize 用户没说填空，startIndex取0，pageSize取10000
                4.playDate默认传当天 字符串类型 yyyy-MM-dd格式
                5. 如果是闲聊，intentCode固定为CHAT，其他字段留空
                6. 时间里的冒号、JSON里的引号和逗号必须是英文半角字符，禁止使用中文全角字符



                输出固定JSON结构示例：

                {

                "intentCode":"PRIZE_RECORD",

                "targetUserId":10001,

                "startTime":"2026-07-01 00:00:00",

                "endTime":"2026-07-23 12:00:00",

                "startIndex":0,

                "pageSize":10000,

                "playDate":"2026-07-23"
                }
                """.formatted(
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole("system");
            chatMessage.setContent(systemPrompt);
            chatMessageList.add(chatMessage);
            sessionContextMap.put(sessionId,chatMessageList);
        }
        return chatMessageList;
    }
    public void appendMessage(String sessionId,ChatMessage chatMessage){
        List<ChatMessage> chatMessageList = getContext(sessionId);
        chatMessageList.add(chatMessage);
        while(chatMessageList.size()>maxMessageSize){
            //跳过对问答机器人角色的定义，删除最少的一条语句
            if(chatMessageList.size()>=2){
                chatMessageList.remove(1);
            }else{
                break;
            }
        }
    }
    public void clearSession(String sessionId){
        sessionContextMap.remove(sessionId);
    }
}
