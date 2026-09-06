package com.example.AI_chatbot.controller;

import com.example.AI_chatbot.controller.Vo.AnswerVo;
import com.example.AI_chatbot.controller.Vo.BaseVo;
import com.example.AI_chatbot.controller.cmd.ChatQueryCmd;
import com.example.AI_chatbot.service.IntentRecognizeService;
import com.example.AI_chatbot.service.McpChatBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat")
@CrossOrigin
public class McpChatBotController {
    @Autowired
    private IntentRecognizeService intentRecognizeService;
    @Autowired
    public McpChatBotService mcpChatBotService;
    @PostMapping("/ask")
    public AnswerVo ask(@RequestBody ChatQueryCmd chatQueryCmd){
        long start = System.currentTimeMillis();
        long end;
        AnswerVo answerVo = new AnswerVo();

        try {
            String answer = intentRecognizeService.chat(chatQueryCmd.getQuestion(), chatQueryCmd.getUserId(),chatQueryCmd.getSessionId());
            answerVo.setAnswer(answer);

            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(200, end - start, true, null);

            answerVo.setBaseVo(baseVo);

            return answerVo;

        } catch (Exception e) {
            log.error("问答失败，question={}", chatQueryCmd.getQuestion(), e);
            end = System.currentTimeMillis();
            BaseVo baseVo = new BaseVo(500, end - start, false, "其它位置异常：" + e.getMessage());
            answerVo.setBaseVo(baseVo);
            return answerVo;
        }
    }
    @PostMapping("/reset")
    public BaseVo reset(String sessionId){
        long start = System.currentTimeMillis();
        long end;

        try {
            mcpChatBotService.resetSession(sessionId);

            end = System.currentTimeMillis();
            return new BaseVo(200, end - start, true, null);

        } catch (Exception e) {
            log.error(e.getMessage());
            end = System.currentTimeMillis();
            return new BaseVo(500, end - start, false, "其它位置异常");
        }
    }

}
