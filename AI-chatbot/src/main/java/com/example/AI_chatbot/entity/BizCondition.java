package com.example.AI_chatbot.entity;

public class BizCondition {
    private String intentCode;
    private Integer targetUserId;
    private String startTime;
    private String endTime;
    private Integer startIndex;
    private Integer pageSize;
    private String playDate;
    private String originQuestion;

    public String getOriginQuestion() {
        return originQuestion;
    }

    public void setOriginQuestion(String originQuestion) {
        this.originQuestion = originQuestion;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getIntentCode() {
        return intentCode;
    }

    public void setIntentCode(String intentCode) {
        this.intentCode = intentCode;
    }

    public Integer getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Integer targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public BizCondition(String intentCode, Integer targetUserId, String startTime, String endTime, Integer pageSize, Integer startIndex, String playDate, String originQuestion) {
        this.intentCode = intentCode;
        this.targetUserId = targetUserId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSize = pageSize;
        this.startIndex = startIndex;
        this.playDate = playDate;
        this.originQuestion = originQuestion;
    }

    public BizCondition() {
    }
}
