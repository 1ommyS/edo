package org.example.utils;

/**
 * @author 1ommy
 * @version 01.11.2023
 */
public class StringUtils {
    public static String multiplyString(String element, int amount) {
        StringBuilder stringBuilder = new StringBuilder(element);

        for (int i = 0; i < amount; i++) {
            stringBuilder.append(element);
        }

        return stringBuilder.toString();
    }
}
