package com.animal.java.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomUtils {

    public static final Random RANDOM = new Random();

    public static String getRandomString(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String getRandomAlphabeticString(int length){
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static Integer getNumberReqLength(int length){
        Random random = new Random();
        String str = String.valueOf(1 + random.nextInt(9));
        for(int i = 0; i < length-1;i++){
            str = str.concat(String.valueOf(random.nextInt(10)));
        }
        return Integer.parseInt(str);
    }

    public static String getNumericString(int length){
        if(length == 0){
            return "";
        }
        if(length == 1){
            return String.valueOf(org.apache.commons.lang3.RandomUtils.nextInt(1,10));
        }
        return org.apache.commons.lang3.RandomUtils.nextInt(1,10) + RandomStringUtils.randomNumeric(length - 1);
    }

    public static Integer getRandomNumber(int maxNumber){
        return RANDOM.nextInt(maxNumber);
    }
}
