package com.example.AI_chatbot.controller.cmd;

public class ChatQueryCmd {
    private String sessionId;
    private String question;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ChatQueryCmd(String sessionId, String question, int userId) {
        this.sessionId = sessionId;
        this.question = question;
        this.userId = userId;
    }

    public ChatQueryCmd() {
    }
}
