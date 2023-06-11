package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import com.mandarin.discord.entity.TriviaAnswer;
import com.mandarin.discord.entity.TriviaQuestion;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class TriviaRepository {

    public TriviaQuestion findRandomQuestion(String group, TriviaQuestion.Complexity complexity) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM gandalf.trivia_questions
                    WHERE `complexity` = ? AND `group` = ?
                    ORDER BY RAND()
                    LIMIT 1
                      """);

            statement.setString(1, complexity.toString().toLowerCase());
            statement.setString(2, group);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            TriviaQuestion triviaQuestion = TriviaQuestion.builder()
                    .id(UUID.fromString(resultSet.getString("id")))
                    .title(resultSet.getString("title"))
                    .color(resultSet.getString("color"))
                    .points(resultSet.getInt("points"))
                    .answerA(resultSet.getString("answer_a"))
                    .answerB(resultSet.getString("answer_b"))
                    .answerC(resultSet.getString("answer_c"))
                    .answerD(resultSet.getString("answer_d"))
                    .correctAnswer(resultSet.getString("correct_answer"))
                    .createdOn(resultSet.getTimestamp("created_on").toLocalDateTime())
                    .author(resultSet.getString("author"))
                    .imageUrl(resultSet.getString("image_url"))
                    .group(resultSet.getString("group"))
                    .complexity(TriviaQuestion.Complexity.valueOf(resultSet.getString("complexity").toUpperCase()))
                    .build();

            connection.close();
            return triviaQuestion;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TriviaAnswer getUserAnswer(String userId, String questionId) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM gandalf.trivia_answers
                    WHERE `user_id` = ? AND `question_id` = ?
                      """);

            statement.setString(1, userId);
            statement.setString(2, questionId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            TriviaAnswer triviaAnswer = TriviaAnswer.builder()
                    .buttonId(resultSet.getString("button_id"))
                    .userId(resultSet.getString("user_id"))
                    .points(resultSet.getInt("points"))
                    .questionId(UUID.fromString(resultSet.getString("question_id")))
                    .correctness(resultSet.getBoolean("correctness"))
                    .userAnswer(resultSet.getString("user_answer"))
                    .correctAnswer(resultSet.getString("correct_answer"))
                    .createdOn(resultSet.getTimestamp("created_on").toLocalDateTime())
                    .build();

            connection.close();
            return triviaAnswer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean doesUserHasAnswer(String userId, String questionId) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT COUNT(*) FROM gandalf.trivia_answers
                    WHERE `user_id` = ? AND `question_id` = ?
                      """);

            statement.setString(1, userId);
            statement.setString(2, questionId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            int answersCount = resultSet.getInt(1);

            connection.close();

            return answersCount != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertAnswer(String userId, String buttonId, String userAnswer, boolean correctness, int points) {

        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.trivia_answers(`button_id`, `user_id`, `points`, `question_id`, `correctness`, `user_answer`, `correct_answer`, `created_on`)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                    """);

            statement.setString(1, buttonId);
            statement.setString(2, userId);
            statement.setInt(3, points);
            statement.setString(4, buttonId.split("\\$")[0]);
            statement.setBoolean(5, correctness);
            statement.setString(6, userAnswer);
            statement.setString(7, buttonId.split("\\$")[3]);
            statement.setTimestamp(8, Timestamp.from(Instant.now()));

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
