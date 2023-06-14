package com.mandarin.discord.json;

import java.util.Map;

public class TriviaQuestionInsertCommand {

    private String question;
    private Map<String, String> options;
    private String correct_answer;

    public TriviaQuestionInsertCommand() {

    }

    public TriviaQuestionInsertCommand(String question, Map<String, String> options, String correct_answer, String difficulty) {
        this.question = question;
        this.options = options;
        this.correct_answer = correct_answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

}
