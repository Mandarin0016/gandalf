package com.mandarin.discord.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class TriviaUserInfo {
    private Map<String, Integer> groupPoints;
    private Map<String, Integer> answeredQuestions;
    private Map<String, Integer> correctAnswers;
    private int globalRank;
    private int localRank;
}
