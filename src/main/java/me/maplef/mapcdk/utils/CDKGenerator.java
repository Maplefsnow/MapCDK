package me.maplef.mapcdk.utils;

import java.util.Random;

public class CDKGenerator {
    private static final String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateCDK(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(36);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }

    public static String generateCDK(String prefix, int lengthOfRandom, String suffix) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lengthOfRandom; i++) {
            int number = random.nextInt(36);
            stringBuilder.append(str.charAt(number));
        }
        return prefix + stringBuilder + suffix;
    }
}
