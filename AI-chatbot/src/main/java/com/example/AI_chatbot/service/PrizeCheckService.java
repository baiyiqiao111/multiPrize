package com.example.AI_chatbot.service;

import com.example.AI_chatbot.Integration.AiChatBotIntegration;
import com.example.AI_chatbot.Integration.vo.PlayRecordVo;
import com.example.AI_chatbot.Integration.vo.PrizeRecordVo;
import com.example.AI_chatbot.controller.Vo.SystemConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 检查用户一段时间内的发奖是否正常，按天执行：
 * 1.查询当天播放记录 2.查询发奖规则 3.按规则计算每阶段预计发奖
 * 4.查询实际发奖记录得到每阶段实际发奖 5.逐阶段比对预计与实际
 * 计算全部在代码里完成，大模型只负责选择日期范围和解释结果，避免大模型自己算错
 */
@Slf4j
@Service
public class PrizeCheckService {
    private static final String PRIZE_STAGE_RULE = "PRIZE_STAGE_RULE";
    private static final String PRIZE_AMOUNT_RULE = "PRIZE_AMOUNT_RULE";
    //只核对播放音乐场景的发奖
    private static final String PLAY_MUSIC_SCENE = "PLAY_MUSIC";
    private static final int MAX_DAYS = 31;
    private static final int PRIZE_PAGE_SIZE = 10000;
    private static final JexlEngine JEXL_ENGINE = new JexlBuilder().create();

    @Autowired
    private AiChatBotIntegration aiChatBotIntegration;

    public PrizeCheckResult check(int userId, String startDateStr, String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            return PrizeCheckResult.fail("日期格式错误，应为yyyy-MM-dd，startDate=" + startDateStr + ", endDate=" + endDateStr);
        }
        //未来的日期没有数据，截止到今天
        LocalDate today = LocalDate.now();
        if (endDate.isAfter(today)) {
            endDate = today;
        }
        if (endDate.isBefore(startDate)) {
            return PrizeCheckResult.fail("结束日期不能早于开始日期");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) + 1 > MAX_DAYS) {
            return PrizeCheckResult.fail("单次最多检查" + MAX_DAYS + "天");
        }

        //2.查询发奖规则
        SystemConfigVo stageRule = aiChatBotIntegration.querySystemConfigVo(PRIZE_STAGE_RULE);
        SystemConfigVo amountRule = aiChatBotIntegration.querySystemConfigVo(PRIZE_AMOUNT_RULE);
        if (stageRule == null || amountRule == null) {
            return PrizeCheckResult.fail("查询发奖规则失败，无法检查");
        }

        //4.一次查出整个范围的发奖记录，按发奖日期分组
        List<PrizeRecordVo> prizeRecords = aiChatBotIntegration.queryPrizeRecordListByTime(
                userId, startDate + " 00:00:00", endDate + " 23:59:59", 0, PRIZE_PAGE_SIZE);
        Map<String, List<PrizeRecordVo>> prizeRecordsByDate = prizeRecords.stream()
                .filter(record -> PLAY_MUSIC_SCENE.equals(record.getScene()))
                .collect(Collectors.groupingBy(PrizeRecordVo::getDateStr));

        List<DailyPrizeCheck> days = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();
            //1.查询当天播放记录
            List<PlayRecordVo> playRecords = aiChatBotIntegration.queryPlayRecordListByTime(
                    userId, dateStr + " 00:00:00", dateStr + " 23:59:59");
            int totalDuration = playRecords.stream().mapToInt(PlayRecordVo::getDuration).sum();
            //3.按规则计算每阶段预计发奖
            Map<Integer, Integer> expected = calculateExpectedPrize(stageRule.getValue(), amountRule.getValue(), totalDuration);
            //4.每阶段实际发奖
            Map<Integer, Integer> actual = new TreeMap<>();
            for (PrizeRecordVo record : prizeRecordsByDate.getOrDefault(dateStr, List.of())) {
                actual.merge(record.getStage(), record.getAmount(), Integer::sum);
            }
            //5.比对
            List<String> differences = compare(expected, actual);
            days.add(new DailyPrizeCheck(dateStr, playRecords.size(), totalDuration,
                    toStageAmounts(expected), toStageAmounts(actual), differences.isEmpty(), differences));
        }
        boolean allMatched = days.stream().allMatch(DailyPrizeCheck::matched);
        log.info("发奖检查完成，userId={}, startDate={}, endDate={}, allMatched={}", userId, startDate, endDate, allMatched);
        return new PrizeCheckResult(true, null, startDate.toString(), endDate.toString(), allMatched,
                stageRule.getValue(), amountRule.getValue(), days);
    }

    /**
     * 模拟 prize_sender 的发奖过程：播放时长逐秒累加，每进入一个新阶段，按当时的时长计算该阶段的发奖数量。
     * 阶段或数量为0时不发奖，与 prize_sender 一致。
     * 例如 240s：第一阶段1份，第二阶段2份
     */
    static Map<Integer, Integer> calculateExpectedPrize(String stageRule, String amountRule, int totalDuration) {
        JexlExpression stageExpression = JEXL_ENGINE.createExpression(stageRule);
        JexlExpression amountExpression = JEXL_ENGINE.createExpression(amountRule);
        Map<Integer, Integer> expected = new TreeMap<>();
        for (int duration = 1; duration <= totalDuration; duration=duration+30) {
            int stage = evaluate(stageExpression, duration);
            if (stage == 0 || expected.containsKey(stage)) {
                continue;
            }
            int amount = evaluate(amountExpression, duration);
            if (amount != 0) {
                expected.put(stage, amount);
            }
        }
        return expected;
    }

    static List<String> compare(Map<Integer, Integer> expected, Map<Integer, Integer> actual) {
        Set<Integer> stages = new TreeSet<>(expected.keySet());
        stages.addAll(actual.keySet());
        List<String> differences = new ArrayList<>();
        for (Integer stage : stages) {
            Integer expectedAmount = expected.get(stage);
            Integer actualAmount = actual.get(stage);
            if (expectedAmount == null) {
                differences.add("第" + stage + "阶段多发：按规则不应发放，实际发放" + actualAmount + "份");
            } else if (actualAmount == null) {
                differences.add("第" + stage + "阶段漏发：预计发放" + expectedAmount + "份，实际未发放");
            } else if (!expectedAmount.equals(actualAmount)) {
                differences.add("第" + stage + "阶段数量不符：预计发放" + expectedAmount + "份，实际发放" + actualAmount + "份");
            }
        }
        return differences;
    }

    private static int evaluate(JexlExpression expression, int totalDuration) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("totalDuration", totalDuration);
        return ((Number) expression.evaluate(new MapContext(variables))).intValue();
    }

    private static List<StageAmount> toStageAmounts(Map<Integer, Integer> stageMap) {
        return stageMap.entrySet().stream()
                .map(entry -> new StageAmount(entry.getKey(), entry.getValue()))
                .toList();
    }

    public record StageAmount(int stage, int amount) {}

    public record DailyPrizeCheck(String date, int playCount, int totalDuration,
                                  List<StageAmount> expectedPrizes, List<StageAmount> actualPrizes,
                                  boolean matched, List<String> differences) {}

    public record PrizeCheckResult(boolean success, String message, String startDate, String endDate,
                                   boolean allMatched, String stageRule, String amountRule,
                                   List<DailyPrizeCheck> days) {
        static PrizeCheckResult fail(String message) {
            return new PrizeCheckResult(false, message, null, null, false, null, null, List.of());
        }
    }
}