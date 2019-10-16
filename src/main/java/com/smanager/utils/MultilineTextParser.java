package com.smanager.utils;

public class MultilineTextParser {
    private final static int MAX_NUMBER_OF_CHARACTERS_IN_STRING = 32;

    public static String getMultilineTextFromString(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return "Input is empty or null!";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int currentLineLength = 0;
        for(String line : inputString.trim().split("\n")) {
            if (line.length() + currentLineLength > MAX_NUMBER_OF_CHARACTERS_IN_STRING) {
                stringBuilder.append("<br>");
                currentLineLength = 0;
                stringBuilder.append(line);
            } else {
                stringBuilder.append(line);
                currentLineLength += line.length();
            }
        }

        return stringBuilder.toString();
    }
}
