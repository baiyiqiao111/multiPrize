package com.example.AI_chatbot.service;

import com.example.AI_chatbot.entity.ChatMessage;
import com.example.AI_chatbot.entity.OpenAIChatRequest;
import com.example.AI_chatbot.entity.OpenAiChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OpenAiClientService {
    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.temperature}")
    private Double temperature;
    @Autowired
    private ObjectMapper objectMapper;
    private OkHttpClient okHttpClient(){
        return  new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .build();
    }

    public String chatCompletion(List<ChatMessage> chatMessageList){
        OpenAIChatRequest request = new OpenAIChatRequest();
        request.setModel(model);
        request.setTemperature(temperature);
        request.setMessages(chatMessageList);
        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonBody,mediaType);
            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            try (Response response = okHttpClient().newCall(httpRequest).execute()) {
                String responseJson = response.body() == null ? "" : response.body().string();
                if (!response.isSuccessful()) {
                    log.error("OpenAI接口调用失败，code={}，body={}", response.code(), responseJson);
                    return null;
                }
                OpenAiChatResponse openAiChatResponse = objectMapper.readValue(responseJson, OpenAiChatResponse.class);
                if (openAiChatResponse.getChoices() == null || openAiChatResponse.getChoices().isEmpty()) {
                    log.error("OpenAI接口返回内容里没有choices，body={}", responseJson);
                    return null;
                }
                return openAiChatResponse.getChoices().get(0).getChatMessage().getContent();
            }
        }catch  (Exception e){
            log.error("OpenAI接口调用失败，url={}，model={}", apiUrl, model, e);
            return null;
        }
    }
}
