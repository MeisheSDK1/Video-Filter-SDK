package com.meishe.sdkdemo.utils;

public class NumberUtils {
    public static int parseString2Int(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long parseString2Long(String longStr) {
        try {
            return Long.parseLong(longStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parseString2Double(String doubleStr) {
        try {
            return Double.parseDouble(doubleStr);
        } catch (Exception e) {
            return 0;
        }
    }
    public static float parseString2Float(String floatStr) {
        try {
            return Float.parseFloat(floatStr);
        } catch (Exception e) {
            return 0f;
        }
    }
}
