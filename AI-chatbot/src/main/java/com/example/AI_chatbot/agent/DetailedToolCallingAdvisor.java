package com.example.AI_chatbot.agent;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.util.Assert;

import java.util.List;
@Slf4j
public class DetailedToolCallingAdvisor  extends ToolCallingAdvisor {

    private final int maxIterations;

    public DetailedToolCallingAdvisor(ToolCallingManager toolCallingManager, int maxIterations) {
        super(toolCallingManager,
                chatResponse -> chatResponse != null && chatResponse.hasToolCalls(),
                DEFAULT_ORDER,
                true);
        Assert.isTrue(maxIterations > 0, "maxIterations must be greater than 0");
        this.maxIterations = maxIterations;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(chain, "chain must not be null");

        if (!(request.prompt().getOptions() instanceof ToolCallingChatOptions options)) {
            return chain.nextCall(request);
        }

        List<Message> instructions = request.prompt().getInstructions();

        for (int iteration = 1; iteration <= this.maxIterations; iteration++) {
            log.info("========== HelloAgent 思考轮次 {}/{} ==========", iteration, this.maxIterations);

            ChatClientRequest iterationRequest = ChatClientRequest.builder()
                    .prompt(new Prompt(instructions, options))
                    .context(request.context())
                    .build();

            ChatClientResponse clientResponse = chain.copy(this).nextCall(iterationRequest);
            ChatResponse chatResponse = clientResponse.chatResponse();
            Assert.notNull(chatResponse, "chatResponse must not be null");

            printModelResponse(chatResponse);

            if (!chatResponse.hasToolCalls()) {
                log.info("HelloAgent 在第 {} 轮完成回答", iteration);
                return clientResponse;
            }

            if (iteration == this.maxIterations) {
                String message = "已达到最大思考轮次上限（" + this.maxIterations + "），停止继续调用工具。";
                log.warn(message);
                ChatResponse limitedResponse = ChatResponse.builder()
                        .from(chatResponse)
                        .generations(List.of(new Generation(new AssistantMessage(message))))
                        .build();
                return clientResponse.mutate().chatResponse(limitedResponse).build();
            }

            ToolExecutionResult executionResult = this.toolCallingManager
                    .executeToolCalls(iterationRequest.prompt(), chatResponse);
            printToolResults(executionResult);

            if (executionResult.returnDirect()) {
                ChatResponse directResponse = ChatResponse.builder()
                        .from(chatResponse)
                        .generations(ToolExecutionResult.buildGenerations(executionResult))
                        .build();
                return clientResponse.mutate().chatResponse(directResponse).build();
            }

            instructions = executionResult.conversationHistory();
        }

        throw new IllegalStateException("Unexpected end of HelloAgent iteration loop");
    }

    private void printModelResponse(ChatResponse chatResponse) {
        for (Generation generation : chatResponse.getResults()) {
            AssistantMessage output = generation.getOutput();
            String content = output.getText();
            log.info("模型输出: {}", content == null || content.isBlank() ? "<无文本输出>" : content);
            log.info("结束原因: {}", generation.getMetadata().getFinishReason());

            for (AssistantMessage.ToolCall toolCall : output.getToolCalls()) {
                log.info("调用工具: name={}, id={}, type={}",
                        toolCall.name(), toolCall.id(), toolCall.type());
                log.info("工具参数: {}", toolCall.arguments());
            }
        }
    }

    private void printToolResults(ToolExecutionResult executionResult) {
        for (Message message : executionResult.conversationHistory()) {
            if (message instanceof ToolResponseMessage toolResponseMessage) {
                for (ToolResponseMessage.ToolResponse response : toolResponseMessage.getResponses()) {
                    log.info("工具结果: name={}, id={}, result={}",
                            response.name(), response.id(), response.responseData());
                }
            }
        }
    }
}
