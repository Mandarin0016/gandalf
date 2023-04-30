package com.mandarin.discord.entity;


import java.time.LocalDate;

public class Exam {
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFinished;
    private String creatorUsername;

    public Exam(String courseName, LocalDate startDate, LocalDate endDate, boolean isFinished, String creatorUsername) {
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFinished = isFinished;
        this.creatorUsername = creatorUsername;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
}
