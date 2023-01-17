package me.maplef.mapcdk.utils;

import me.maplef.mapcdk.CDK;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDKGenerator {

    private static final String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String generateCDKbyLength(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(36);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }

    public static String generateCDKbyFormat(String format) {
        String CDKString = format;

        Pattern pattern = Pattern.compile("\\*+");
        Matcher matcher = pattern.matcher(CDKString);

        do{
            CDKString = format;
            while (matcher.find()){
                CDKString = CDKString.replaceFirst("\\*+", generateCDKbyLength(matcher.group().length()));
            }
        }while (!checkCDK(CDKString));

        return CDKString;
    }

    private static boolean checkCDK(String CDK) {
        try(Statement stmt = new Database().getC().createStatement();
            ResultSet res = stmt.executeQuery(String.format("SELECT * FROM cdk_info WHERE cdk_string = '%s'", CDK))) {
            return !res.next();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
