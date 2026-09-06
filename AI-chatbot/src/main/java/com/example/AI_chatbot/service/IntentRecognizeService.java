package com.example.AI_chatbot.service;

import com.example.AI_chatbot.Integration.AiChatBotIntegration;
import com.example.AI_chatbot.Integration.vo.PlayRecordVo;
import com.example.AI_chatbot.Integration.vo.PrizeRecordVo;
import com.example.AI_chatbot.context.McpProfessionalContextManager;
import com.example.AI_chatbot.entity.BizCondition;
import com.example.AI_chatbot.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IntentRecognizeService {
    @Autowired
    private McpProfessionalContextManager mcpProfessionalContextManager;
    @Autowired
    private OpenAiClientService openAiClientService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AiChatBotIntegration aiChatBotIntegration;
    public String chat(String question,int userId,String sessionId){
        BizCondition recognize = recognize(question, userId, sessionId);
        String answer = queryResult(recognize);
        return convertToText(question,answer);
    }
    public BizCondition recognize(String question,int userId,String sessionId){
        ChatMessage questionChatMessage = new ChatMessage();
        questionChatMessage.setRole("user");
        questionChatMessage.setContent(question);
        mcpProfessionalContextManager.appendMessage(sessionId,questionChatMessage);
        List<ChatMessage> context = mcpProfessionalContextManager.getContext(sessionId);
        String answer = openAiClientService.chatCompletion(context);
        if(answer==null||answer.isBlank()){
            throw new IllegalStateException("大模型没有返回内容，请检查OpenAI接口是否可用（日志里有具体原因）");
        }
        log.info("大模型返回的意图JSON：{}", answer);
        BizCondition bizCondition;
        try {
            bizCondition = objectMapper.readValue(stripCodeFence(answer),BizCondition.class);
        }catch (Exception e){
            throw new IllegalStateException("大模型返回的内容不是合法JSON："+answer, e);
        }
        bizCondition.setOriginQuestion(question);
        bizCondition.setTargetUserId(userId);
        ChatMessage answerChatMessage = new ChatMessage();
        answerChatMessage.setRole("assistant");
        answerChatMessage.setContent(answer);
        mcpProfessionalContextManager.appendMessage(sessionId,answerChatMessage);
        return bizCondition;
    }
    public String queryResult(BizCondition bizCondition){
        String intentCode = bizCondition.getIntentCode();
        if("PLAY_RECORD".equals(intentCode)){
            List<PlayRecordVo> playRecordVos = aiChatBotIntegration.queryPlayRecordListByTime(bizCondition.getTargetUserId(), bizCondition.getStartTime(), bizCondition.getEndTime());
            return objectMapper.writeValueAsString(playRecordVos);
        }else if("PRIZE_RECORD".equals(intentCode)){
            //模型可能漏掉分页参数，这里兜底，避免Integer拆箱空指针
            int startIndex = bizCondition.getStartIndex()==null?0:bizCondition.getStartIndex();
            int pageSize = bizCondition.getPageSize()==null?10000:bizCondition.getPageSize();
            List<PrizeRecordVo> prizeRecordVos = aiChatBotIntegration.queryPrizeRecordListByTime(bizCondition.getTargetUserId(), bizCondition.getStartTime(), bizCondition.getEndTime(),startIndex,pageSize);
            return objectMapper.writeValueAsString(prizeRecordVos);
        }else {
            return "没有理解你的意图";
        }
    }
    public String convertToText(String question,String answer){
        String systemPrompt = """
         你是智能数据助手，严格遵守以下规则：
         1. 不要输出原始JSON、不要markdown代码块；
         2. 根据【用户提问】和【查询数据】，整理成通俗易懂的中文回答；
         3. 如果查询结果为空、没有记录，直接礼貌告知：未查询到对应记录；
         4. 可以适当做简单汇总，例如记录条数、总额，但不要编造不存在的数据；
         5. 回答简洁干净，不要多余开场白。
         6. 多条记录已表格的方式呈现
         【用户提问】是%s,
         【查询数据】是%s
        """.formatted(question,answer);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");
        chatMessage.setContent(systemPrompt);
        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessage);
        return openAiClientService.chatCompletion(chatMessageList);
    }
    /**
     * 模型有时会把JSON包在```json ... ```里，去掉围栏再解析
     */
    private String stripCodeFence(String answer){
        String trimmed = answer.trim();
        if(!trimmed.startsWith("```")){
            return trimmed;
        }
        int firstLineEnd = trimmed.indexOf('\n');
        if(firstLineEnd<0){
            return trimmed;
        }
        String withoutHeader = trimmed.substring(firstLineEnd+1);
        int lastFence = withoutHeader.lastIndexOf("```");
        return (lastFence<0?withoutHeader:withoutHeader.substring(0,lastFence)).trim();
    }
}