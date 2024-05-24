package com.oroplatform.idea.oroplatform.util;

public class OroPlatformStringUtil {
    public static String snakeToCamel(String snakeCase) {
        String[] words = snakeCase.split("_");
        for (int i = 1; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join("", words);
    }

    public static String capitalizeFirstCharacter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
