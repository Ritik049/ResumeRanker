package com.resume.matcher.util;

import java.util.*;
import java.util.stream.Collectors;

public class KeyWordExtractor {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "is", "and", "a", "an", "to", "in", "on", "for", "with", "of", "at", "by", "from", "as", "this", "that"
    );

    public static List<String> extractKeywords(String text, int topN) {
        Map<String, Integer> freqMap = new HashMap<>();

        String[] words = text.toLowerCase().split("\\W+");

        for (String word : words) {
            if (word.length() < 3 || STOP_WORDS.contains(word)) continue;
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        return freqMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
