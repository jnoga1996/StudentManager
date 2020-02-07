package com.smanager;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static Map<Long, String> userUrlCache = new HashMap<>();
    private static Map<Long, String> divCache = new HashMap<>();

    public static void put(Long userId, String previousUrl) {
        if (userUrlCache.containsKey(userId)) {
            userUrlCache.remove(userId);
        }
        userUrlCache.put(userId, previousUrl);
    }

    public static String get(Long userId) {
        if (userUrlCache.containsKey(userId)) {
            return userUrlCache.get(userId);
        }
        return null;
    }

    public static void putDiv(Long userId, String divId) {
        if (divCache.containsKey(userId)) {
            divCache.remove(userId);
        }
        divCache.put(userId, divId);
    }

    public static String getDiv(Long userId) {
        if (divCache.containsKey(userId)) {
            return divCache.get(userId);
        }
        return null;
    }
}
