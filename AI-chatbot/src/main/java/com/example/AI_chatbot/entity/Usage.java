package com.example.AI_chatbot.entity;

public class Usage {
    private Integer prompt_tokens;
    private Integer completion_tokens;
    private Integer total_tokens;
    private PromptTokensDetails prompt_tokens_details;
    private CompletionTokensDetails completion_tokens_details;

    public Integer getPrompt_tokens() {
        return prompt_tokens;
    }

    public void setPrompt_tokens(Integer prompt_tokens) {
        this.prompt_tokens = prompt_tokens;
    }

    public Integer getCompletion_tokens() {
        return completion_tokens;
    }

    public void setCompletion_tokens(Integer completion_tokens) {
        this.completion_tokens = completion_tokens;
    }

    public Integer getTotal_tokens() {
        return total_tokens;
    }

    public void setTotal_tokens(Integer total_tokens) {
        this.total_tokens = total_tokens;
    }

    public PromptTokensDetails getPrompt_tokens_details() {
        return prompt_tokens_details;
    }

    public void setPrompt_tokens_details(PromptTokensDetails prompt_tokens_details) {
        this.prompt_tokens_details = prompt_tokens_details;
    }

    public CompletionTokensDetails getCompletion_tokens_details() {
        return completion_tokens_details;
    }

    public Usage(Integer prompt_tokens, Integer completion_tokens, Integer total_tokens, PromptTokensDetails prompt_tokens_details, CompletionTokensDetails completion_tokens_details) {
        this.prompt_tokens = prompt_tokens;
        this.completion_tokens = completion_tokens;
        this.total_tokens = total_tokens;
        this.prompt_tokens_details = prompt_tokens_details;
        this.completion_tokens_details = completion_tokens_details;
    }

    public void setCompletion_tokens_details(CompletionTokensDetails completion_tokens_details) {
        this.completion_tokens_details = completion_tokens_details;
    }

    public Usage() {
    }
}
