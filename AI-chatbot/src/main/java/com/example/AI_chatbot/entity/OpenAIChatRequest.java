package com.example.AI_chatbot.entity;

import java.util.List;

public class OpenAIChatRequest {
    private String model;
    private List<ChatMessage> messages;
    private Double temperature;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public OpenAIChatRequest(String model, List<ChatMessage> messages, Double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public OpenAIChatRequest() {
    }
}
