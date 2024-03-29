package com.maveric.digital.utils;

public class RandomIdGeneratorUtil {
    private RandomIdGeneratorUtil() {
    }

    public static String generatePKId(String text) {
        long currentMills = System.currentTimeMillis();
        return text.concat( String.valueOf(currentMills));
    }

}
