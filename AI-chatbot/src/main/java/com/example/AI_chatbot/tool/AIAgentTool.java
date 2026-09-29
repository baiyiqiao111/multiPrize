package com.example.AI_chatbot.tool;

import com.example.AI_chatbot.Integration.AiChatBotIntegration;
import com.example.AI_chatbot.Integration.vo.PlayRecordVo;
import com.example.AI_chatbot.Integration.vo.PrizeRecordVo;
import com.example.AI_chatbot.controller.Vo.SystemConfigVo;
import com.example.AI_chatbot.service.IntentRecognizeService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIAgentTool {
    @Autowired
    private AiChatBotIntegration aiChatBotIntegration;
    @Tool(name = "queryPlayRecord",description = "当需要查询用户指定时间范围内的播放记录时调用")
    public List<PlayRecordVo> queryPlayRecord(//@ToolParam 定义工具的参数提示词，参数获取规则
                                              @ToolParam(description = "播放范围开始，当用户指定某一天默认使用这一天的00:00:00作为开始时间")String startTime,
                                              @ToolParam(description = "播放范围结束，当用户未指定结束时间时，默认使用当前时间")String endTime,
                                              //ToolContext前面没有@ToolParam，说明参数不来源于大模型，来自于前端。
                                              ToolContext toolContext){
        //工具的执行过程，大模型不干涉，大模型工具选择，不干涉工具执行过程
        return aiChatBotIntegration.queryPlayRecordListByTime((Integer) toolContext.getContext().get("userId"), startTime, endTime);
    }
    @Tool(name = "queryPrizeRecord", description = "当需要查询用户指定时间范围内的发奖记录时调用,返回用户的指定时间范围内所有发奖记录")
    public List<PrizeRecordVo> queryPrizeRecord(ToolContext toolContext,
                                                @ToolParam(description = "要查询的发奖开始时间,当用户指定某一天默认使用这一天的00:00:00作为开始时间")String startTime,
                                                @ToolParam(description = "要查询的发奖结束时间，当用户未指定结束时间时，默认使用当前时间")String endTime,
                                                @ToolParam(description = "开始页面，当用户未指定的时候，默认0开始")int pageStart,
                                                @ToolParam(description = "页面大小，当用户未指定的时候，默认为10000")int pageSize){
        return aiChatBotIntegration.queryPrizeRecordListByTime((Integer) toolContext.getContext().get("userId"),startTime,endTime,pageStart,pageSize);
    }
    @Tool(name = "querySystemConfig",description = "当需要查询指定编码的系统配置时调用，在当前场景下用于查询发奖规则")
    public SystemConfigVo querySystemConfig(//@ToolParam 定义工具的参数提示词，参数获取规则
                                            @ToolParam(description = "规则编码，在此场景下规则编码只能是PRIZE_AMOUNT_RULE和PRIZE_STAGE_RULE，其中PRIZE_AMOUNT_RULE代表每一个时长和发奖数量的对应规则，PRIZE_STAGE_RULE代表每一个阶段的对应规则")String code) {
        //工具的执行过程，大模型不干涉，大模型工具选择，不干涉工具执行过程
        return aiChatBotIntegration.querySystemConfigVo(code);
    }
}
