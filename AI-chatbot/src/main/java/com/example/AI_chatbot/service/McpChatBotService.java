package com.example.AI_chatbot.service;

import com.example.AI_chatbot.context.McpContextManager;
import com.example.AI_chatbot.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class McpChatBotService {
    @Autowired
    private McpContextManager mcpContextManager;
    @Autowired
    private  OpenAiClientService openAiClientService;
    public  String chat(String sessionId, String question, int userId){
        ChatMessage questionChatMessage = new ChatMessage();
        questionChatMessage.setRole("user");
        questionChatMessage.setContent(question);
        mcpContextManager.appendMessage(sessionId,questionChatMessage);
        List<ChatMessage> context = mcpContextManager.getContext(sessionId);
        String answer = openAiClientService.chatCompletion(context);
        ChatMessage answerChatMessage = new ChatMessage();
        answerChatMessage.setRole("assistant");
        answerChatMessage.setContent(answer);
        mcpContextManager.appendMessage(sessionId,answerChatMessage);
        return answer;
    }
    public void resetSession(String sessionId){
        mcpContextManager.clearSession(sessionId);
    }

}
