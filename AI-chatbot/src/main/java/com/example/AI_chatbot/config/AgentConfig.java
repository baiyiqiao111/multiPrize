package com.example.AI_chatbot.config;

import com.example.AI_chatbot.agent.DetailedToolCallingAdvisor;
import com.example.AI_chatbot.agent.MusicAgent;
import com.example.AI_chatbot.tool.AIAgentTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Configuration
public class AgentConfig {
    @Bean
    /**
     * @Param ChatClient：和大模型交流的客户端构造器。
     * @Param AIAgentTool：提供给 AI 使用的 Java 函数集合。（及我们给ai使用的工具）
     * @Param ToolCallingManager：工具调用调度器
     * @Param ChatMemoryMapper：上下文记忆
     * @Param music-agent.max-iterations:10 ： 大模型思考轮数为10次
     */
    public MusicAgent musicAgent(
            ChatClient.Builder chatClientBuilder,
            AIAgentTool aiAgentTool,
            ToolCallingManager toolCallingManager,
            ChatMemory chatMemory,
            @Value("${music-agent.max-iterations:10}") int maxIterations) {

        //创建 DetailedToolCallingAdvisor，让 AI 能进行多轮 Tool Calling，而不是调用一次工具就结束。
        DetailedToolCallingAdvisor detailedToolCallingAdvisor =
                new DetailedToolCallingAdvisor(toolCallingManager, maxIterations);
        //创建大模型的记忆管理器，用于存储上下文以获取更有效的回答
        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor.builder(chatMemory).build();

        ChatClient chatClient = chatClientBuilder
                //相当于设置角色身份
                .defaultSystem("""
                当前时间是：{currentTime}（今天是 {today}，{dayOfWeek}）。
                用户提到今天、昨天、本周、最近7天等相对时间，或者没有指定结束时间时，都以这个时间为准换算，
                调用工具时时间格式为 yyyy-MM-dd HH:mm:ss，日期格式为 yyyy-MM-dd。

                你是一个助手，可以使用工具查找用户需要的数据，
                把ToolService里面的返回结果封装成自然语言给用户。
                在处理结果时，注意：
                    1. 不要输出原始JSON、不要markdown代码块；
                    2. 根据【用户提问】和【查询数据】，整理成通俗易懂的中文回答；
                    3. 如果查询结果为空、没有记录，直接礼貌告知：未查询到对应记录；
                    4. 可以适当做简单汇总，例如记录条数、总额，但不要编造不存在的数据；
                    5. 回答简洁干净，不要多余开场白。
                    6. 当用户提出模糊问题时，比如“为什么我收到的奖励不对”，“为什么我的发奖异常”，
                    “为什么我得到的奖励数量变少/变多”“这几天发奖正常吗”等问题时，直接调用检查发奖记录工具(checkPrizeRecord)，
                    该工具已完成播放记录查询、规则查询、预计发奖计算和比对，不要自己重新计算预计发奖，以工具返回的结果为准，
                    并向用户解释说明当前用户听了多久，预计应得多少奖励，实际得到了多少奖励，当前规则是什么，如果发奖不对，说明是哪天哪个阶段不对
                """)
                //添加指定调用大模型的默认参数，比如：temperature，model，maxTokens，parallelToolCalls
                .defaultOptions(OpenAiChatOptions.builder()
                        .parallelToolCalls(true))
                //默认的工具集
                .defaultTools(aiAgentTool)
                //默认建议器用于指定上下文记忆建议器，工具调用记忆器
                .defaultAdvisors(
                        memoryAdvisor,
                        detailedToolCallingAdvisor)
                .build();

        return (sessionId, userInput, userId) -> {
            log.info("userId = {}, sessionId = {}",userId,sessionId);
            // 避免不同用户碰巧使用相同 sessionId 导致上下文串线
            String conversationId = userId + ":" + sessionId;
            //模型自己不知道"今天"是几号，每次请求都把当前时间告诉它，否则"查今天"会算错日期
            LocalDateTime now = LocalDateTime.now();

            return chatClient
                    //创建这一次用户请求。
                    .prompt()
                    //只填充 defaultSystem 里的时间占位符，系统提示词本身不变
                    .system(system -> system
                            .param("currentTime", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .param("today", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .param("dayOfWeek", now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINA)))
                    //设置用户问题
                    .user(userInput)
                    //这里是在给 Advisor 传递本次请求特有的参数。比如上下文记忆的位置和会话id等
                    .advisors(advisor -> advisor.param(
                            ChatMemory.CONVERSATION_ID,
                            conversationId))
                    //给 Tool 提供上下文参数。大模型有的参数无法直接获取，调用工具需要涉及，必须通过上下文传递
                    .toolContext(Map.of(
                            "sessionId", sessionId,
                            "userId", userId))
                    //真正调用模型
                    .call()
                    //将大模型获取所需要的答案
                    .content();
        };
    }
}