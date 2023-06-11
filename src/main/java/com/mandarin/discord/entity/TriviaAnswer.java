package com.mandarin.discord.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TriviaAnswer {

    private String buttonId;
    private String userId;
    private int points;
    private UUID questionId;
    private boolean correctness;
    private String userAnswer;
    private String correctAnswer;
    private LocalDateTime createdOn;
}
