package com.example.AI_chatbot.entity;

public class Choice {
    private ChatMessage message;
    private String finish_reason;
    private Integer index;

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public ChatMessage getChatMessage() {
        return message;
    }

    public void setChatMessage(ChatMessage message) {
        this.message = message;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }


    public Choice(ChatMessage message, String finish_reason, Integer index) {
        this.message = message;
        this.finish_reason = finish_reason;
        this.index = index;
    }

    public Choice() {
    }
}
