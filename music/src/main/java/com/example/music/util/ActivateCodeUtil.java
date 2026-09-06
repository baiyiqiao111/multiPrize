package com.example.music.util;

import java.util.Random;

public class ActivateCodeUtil {
    public static String generate(){
        Random random = new Random();
        int value = random.nextInt(900000)+100000;
        String s = String.valueOf(value);
        return s;

    }
}
