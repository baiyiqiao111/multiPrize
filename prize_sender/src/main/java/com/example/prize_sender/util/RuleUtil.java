package com.example.prize_sender.util;

import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.internal.Engine;

import java.util.HashMap;
import java.util.Map;

public class RuleUtil {
    public static int test(int a, int b){
        JexlEngine jexlEngine = new JexlBuilder().create();
        String exp = "a+b";
        Map<String, Object> map = new HashMap<>();
        map.put("a",a);
        map.put("b",b);
        JexlExpression jexlExpression = jexlEngine.createExpression(exp);
        JexlContext jexlContext = new MapContext(map);
        Object object = jexlExpression.evaluate(jexlContext);
        return (int) object;
    }

//    public static void main(String[] args) {
//        int test = test(2, 3);
//        System.out.println(test);
//    }
    public  static int calculatePrizeStage(String rule,int totalDuration){
        JexlEngine jexlEngine = new JexlBuilder().create();
        Map<String, Object> map = new HashMap<>();
        map.put("totalDuration",totalDuration);
        JexlExpression jexlExpression = jexlEngine.createExpression(rule);
        JexlContext jexlContext = new MapContext(map);
        Object object = jexlExpression.evaluate(jexlContext);
        return (int) object;
    }
    public  static int calculatePrizeAmount(String rule,int totalDuration){
        JexlEngine jexlEngine = new JexlBuilder().create();
        Map<String, Object> map = new HashMap<>();
        map.put("totalDuration",totalDuration);
        JexlExpression jexlExpression = jexlEngine.createExpression(rule);
        JexlContext jexlContext = new MapContext(map);
        Object object = jexlExpression.evaluate(jexlContext);
        return (int) object;
    }
}


