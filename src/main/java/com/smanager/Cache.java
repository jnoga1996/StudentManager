package com.smanager;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static Map<Long, String> userUrlCache = new HashMap<>();

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
}
