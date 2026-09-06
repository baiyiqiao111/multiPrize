package com.example.AI_chatbot.controller.Vo;

public class AnswerVo {
    private BaseVo baseVo;
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public BaseVo getBaseVo() {
        return baseVo;
    }

    public void setBaseVo(BaseVo baseVo) {
        this.baseVo = baseVo;
    }

    public AnswerVo(BaseVo baseVo, String answer) {
        this.baseVo = baseVo;
        this.answer = answer;
    }

    public AnswerVo() {
    }
}
