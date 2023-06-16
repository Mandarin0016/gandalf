package com.mandarin.discord.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TriviaQuestion {

    private UUID id;
    private String title;
    private String color;
    private int points;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctAnswer;
    private LocalDateTime createdOn;
    private String author;
    private String imageUrl;
    private String group;
    private Complexity complexity;

    public enum Complexity {
        EASY,
        MEDIUM,
        HARD
    }

}
