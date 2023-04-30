package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import com.mandarin.discord.entity.Exam;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExamRepository {

    public void insert(String courseName, LocalDate startDate, LocalDate endDate, boolean isFinished, String username) {

        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.exam_information(`course`, `start_date`, `end_date`, `is_finished`, `user`)
                    VALUES (?, ?, ?, ?, ?);
                    """);

            statement.setString(1, courseName);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            statement.setBoolean(4, isFinished);
            statement.setString(5, username);

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Exam findValidUpcomingExam() {

        Exam exam = null;

        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT *
                    FROM `gandalf`.`exam_information`
                    WHERE `end_date` >= CURDATE() AND `is_finished` IS FALSE
                    ORDER BY `start_date`, `end_date`
                    LIMIT 1;
                        """);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                exam = new Exam(
                        resultSet.getString("course"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBoolean("is_finished"),
                        resultSet.getString("user"));
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exam;
    }

    public void updateExamStatus() {
        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE `exam_information`
                    SET `is_finished` = true
                    WHERE `end_date` < CURDATE() AND `is_finished` = false;
                        """);

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Exam> findAllReady() {

        List<Exam> exams = new ArrayList<>();

        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT *
                    FROM `gandalf`.`exam_information`
                    WHERE `is_finished` IS FALSE
                    ORDER BY `start_date`, `end_date`;
                        """);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                Exam currentExam = new Exam(
                        resultSet.getString("course"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBoolean("is_finished"),
                        resultSet.getString("user"));

                exams.add(currentExam);
            }

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exams;
    }
}
