package com.example.AI_chatbot.entity;

public class PromptTokensDetails {
    private Integer cached_tokens;
    private Integer audio_tokens;

    public Integer getCached_tokens() {
        return cached_tokens;
    }

    public void setCached_tokens(Integer cached_tokens) {
        this.cached_tokens = cached_tokens;
    }

    public Integer getAudio_tokens() {
        return audio_tokens;
    }

    public void setAudio_tokens(Integer audio_tokens) {
        this.audio_tokens = audio_tokens;
    }

    public PromptTokensDetails(Integer cached_tokens, Integer audio_tokens) {
        this.cached_tokens = cached_tokens;
        this.audio_tokens = audio_tokens;
    }

    public PromptTokensDetails() {
    }
}