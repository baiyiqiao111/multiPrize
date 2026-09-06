package com.example.AI_chatbot.entity;

public class CompletionTokensDetails {
    private Integer reasoning_tokens;
    private Integer audio_tokens;
    private Integer accepted_prediction_tokens;
    private Integer rejected_prediction_tokens;

    public Integer getReasoning_tokens() {
        return reasoning_tokens;
    }

    public void setReasoning_tokens(Integer reasoning_tokens) {
        this.reasoning_tokens = reasoning_tokens;
    }

    public Integer getAudio_tokens() {
        return audio_tokens;
    }

    public void setAudio_tokens(Integer audio_tokens) {
        this.audio_tokens = audio_tokens;
    }

    public Integer getAccepted_prediction_tokens() {
        return accepted_prediction_tokens;
    }

    public void setAccepted_prediction_tokens(Integer accepted_prediction_tokens) {
        this.accepted_prediction_tokens = accepted_prediction_tokens;
    }

    public Integer getRejected_prediction_tokens() {
        return rejected_prediction_tokens;
    }

    public void setRejected_prediction_tokens(Integer rejected_prediction_tokens) {
        this.rejected_prediction_tokens = rejected_prediction_tokens;
    }

    public CompletionTokensDetails(Integer reasoning_tokens, Integer audio_tokens, Integer accepted_prediction_tokens, Integer rejected_prediction_tokens) {
        this.reasoning_tokens = reasoning_tokens;
        this.audio_tokens = audio_tokens;
        this.accepted_prediction_tokens = accepted_prediction_tokens;
        this.rejected_prediction_tokens = rejected_prediction_tokens;
    }

    public CompletionTokensDetails() {
    }
}