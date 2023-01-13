package me.maplef.mapcdk.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDKGenerator {
    private static final FileConfiguration config = new ConfigManager().getConfig();

    private static final String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String generateCDKbyLength(int length) {
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
    public static String generateCDKbyFormat(String format) {
        String cdkString = config.getString("CDK-format", "**********");
        Pattern pattern = Pattern.compile("\\*+");
        Matcher matcher = pattern.matcher(cdkString);

        while (matcher.find()){
            cdkString = cdkString.replaceFirst("\\*+", generateCDKbyLength(matcher.group().length()));
        }

        return cdkString;
    }
}
