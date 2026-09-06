package com.example.AI_chatbot.context;

import com.example.AI_chatbot.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class McpContextManager {
    @Value("${openai.max-message-size}")
    private Integer maxMessageSize;
    private final Map<String, List<ChatMessage>> sessionContextMap = new ConcurrentHashMap<>();


    public List<ChatMessage> getContext(String sessionId){
        List<ChatMessage> chatMessageList= sessionContextMap.get(sessionId);
        if(chatMessageList==null){
            chatMessageList = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole("system");
            chatMessage.setContent("你是一个专业问答机器人，回答简洁清晰，逻辑严谨。");
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
