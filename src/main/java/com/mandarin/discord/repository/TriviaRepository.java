package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import com.mandarin.discord.entity.TriviaAnswer;
import com.mandarin.discord.entity.TriviaQuestion;
import com.mandarin.discord.entity.TriviaUserInfo;
import com.mandarin.discord.json.TriviaQuestionInsertCommand;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

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

    public TriviaUserInfo getUserPoints(String userId) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT `tq`.`group`       AS 'group',
                           SUM(`ta`.`points`) AS 'points',
                           COUNT(*)           AS 'answered_questions',
                           SUM(`correctness`) AS 'correct_answers'
                    FROM `gandalf`.`trivia_questions` AS `tq`
                             LEFT JOIN `gandalf`.`trivia_answers` `ta` ON `ta`.`question_id` = `tq`.`id`
                    WHERE `ta`.`user_id` = ?
                    GROUP BY `tq`.`group`;
                      """);

            statement.setString(1, userId);


            ResultSet resultSet = statement.executeQuery();

            TriviaUserInfo triviaUserInfo = TriviaUserInfo.builder().groupPoints(new HashMap<>()).answeredQuestions(new HashMap<>()).correctAnswers(new HashMap<>()).build();

            while (resultSet.next()) {
                triviaUserInfo.getGroupPoints().put(findGroupDisplayName(resultSet.getString("group")), resultSet.getInt("points"));
                triviaUserInfo.getAnsweredQuestions().put(findGroupDisplayName(resultSet.getString("group")), resultSet.getInt("answered_questions"));
                triviaUserInfo.getCorrectAnswers().put(findGroupDisplayName(resultSet.getString("group")), resultSet.getInt("correct_answers"));
            }

            fillGaps(triviaUserInfo);

            connection.close();

            return triviaUserInfo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getGlobalRankList() {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement globalRankingQuery = connection.prepareStatement("""
                    SELECT SUM(`ta`.`points`) AS 'points',
                           COUNT(*)           AS 'answered_questions',
                           SUM(`correctness`) AS 'correct_answers',
                           `user_id`          AS 'user_id'
                    FROM `gandalf`.`trivia_questions` AS `tq`
                             LEFT JOIN `gandalf`.`trivia_answers` `ta` ON `ta`.`question_id` = `tq`.`id`
                    WHERE `user_id` IS NOT NULL
                    GROUP BY `ta`.`user_id`
                    ORDER BY `points` DESC, `correct_answers` DESC, `answered_questions` DESC, `user_id` DESC
                    LIMIT 10;
                    """);

            ResultSet resultSetForGlobalRanking = globalRankingQuery.executeQuery();

            Set<String> globalRankList = new LinkedHashSet<>();

            while (resultSetForGlobalRanking.next()) {
                String currentUserId = resultSetForGlobalRanking.getString(4);
                int totalPoints = resultSetForGlobalRanking.getInt(1);
                globalRankList.add(currentUserId + "$" + totalPoints);
            }

            connection.close();

            return globalRankList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Set<String>> getGroupRankList() {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement groupRankingQuery = connection.prepareStatement("""
                    SELECT `tq`.`group`         AS 'group',
                           SUM(`ta`.`points`)   AS 'points',
                           COUNT(*)             AS 'answered_questions',
                           (SELECT SUM(`tagp`.`points`)
                            FROM `gandalf`.`trivia_answers` AS `tagp`
                            WHERE `tagp`.`user_id` = `ta`.`user_id`
                            GROUP BY `user_id`) AS `total_points`,
                           SUM(`correctness`)   AS 'correct_answers',
                           `user_id`            AS 'user_id'
                    FROM `gandalf`.`trivia_questions` AS `tq`
                             LEFT JOIN `gandalf`.`trivia_answers` `ta` ON `ta`.`question_id` = `tq`.`id`
                    WHERE `user_id` IS NOT NULL
                    GROUP BY `tq`.`group`, `ta`.`user_id`
                    ORDER BY `points` DESC, `correct_answers` DESC, `answered_questions` DESC, `total_points` DESC, `user_id` DESC;                                                                   
                      """);

            ResultSet resultSetForGroupRanking = groupRankingQuery.executeQuery();

            Set<String> javaRankList = new LinkedHashSet<>();
            Set<String> pythonRankList = new LinkedHashSet<>();
            Set<String> cSharpRankList = new LinkedHashSet<>();
            Set<String> jsRankList = new LinkedHashSet<>();

            while (resultSetForGroupRanking.next()) {

                String currentUserId = resultSetForGroupRanking.getString(6);
                int points = resultSetForGroupRanking.getInt(2);
                String group = resultSetForGroupRanking.getString(1);

                switch (group) {
                    case "java" -> javaRankList.add(currentUserId + "$" + points);
                    case "c#" -> cSharpRankList.add(currentUserId + "$" + points);
                    case "python" -> pythonRankList.add(currentUserId + "$" + points);
                    case "js" -> jsRankList.add(currentUserId + "$" + points);
                    default -> throw new RuntimeException("Trivia answer given with unsupported group!" +
                            " Can't make the ranking! Given group: " + group);
                }
            }

            connection.close();

            return List.of(javaRankList, pythonRankList, cSharpRankList, jsRankList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getUserRank(String userId, List<String> roles) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement globalRankingQuery = connection.prepareStatement("""
                    SELECT SUM(`ta`.`points`) AS 'points',
                           COUNT(*)           AS 'answered_questions',
                           SUM(`correctness`) AS 'correct_answers',
                           `user_id`          AS 'user_id'
                    FROM `gandalf`.`trivia_questions` AS `tq`
                             LEFT JOIN `gandalf`.`trivia_answers` `ta` ON `ta`.`question_id` = `tq`.`id`
                    WHERE `user_id` IS NOT NULL
                    GROUP BY `ta`.`user_id`
                    ORDER BY `points` DESC, `correct_answers` DESC, `answered_questions` DESC, `user_id` DESC;
                    """);

            PreparedStatement groupRankingQuery = connection.prepareStatement("""
                    SELECT `tq`.`group`         AS 'group',
                           SUM(`ta`.`points`)   AS 'points',
                           COUNT(*)             AS 'answered_questions',
                           (SELECT SUM(`tagp`.`points`)
                            FROM `gandalf`.`trivia_answers` AS `tagp`
                            WHERE `tagp`.`user_id` = `ta`.`user_id`
                            GROUP BY `user_id`) AS `total_points`,
                           SUM(`correctness`)   AS 'correct_answers',
                           `user_id`            AS 'user_id'
                    FROM `gandalf`.`trivia_questions` AS `tq`
                             LEFT JOIN `gandalf`.`trivia_answers` `ta` ON `ta`.`question_id` = `tq`.`id`
                    WHERE `user_id` IS NOT NULL
                    GROUP BY `tq`.`group`, `ta`.`user_id`
                    ORDER BY `points` DESC, `correct_answers` DESC, `answered_questions` DESC, `total_points` DESC, `user_id` DESC;                                                                   
                      """);


            ResultSet resultSetForGlobalRanking = globalRankingQuery.executeQuery();
            ResultSet resultSetForGroupRanking = groupRankingQuery.executeQuery();


            Set<String> globalRankList = new LinkedHashSet<>();
            Set<String> javaRankList = new LinkedHashSet<>();
            Set<String> pythonRankList = new LinkedHashSet<>();
            Set<String> cSharpRankList = new LinkedHashSet<>();
            Set<String> jsRankList = new LinkedHashSet<>();

            while (resultSetForGlobalRanking.next()) {
                String currentUserId = resultSetForGlobalRanking.getString(4);
                globalRankList.add(currentUserId);
            }

            while (resultSetForGroupRanking.next()) {

                String currentUserId = resultSetForGroupRanking.getString(6);
                String group = resultSetForGroupRanking.getString(1);

                switch (group) {
                    case "java" -> javaRankList.add(currentUserId);
                    case "c#" -> cSharpRankList.add(currentUserId);
                    case "python" -> pythonRankList.add(currentUserId);
                    case "js" -> jsRankList.add(currentUserId);
                    default -> throw new RuntimeException("Trivia answer given with unsupported group!" +
                            " Can't make the ranking! Given group: " + group);
                }
            }

            Map<String, String> ranks = new LinkedHashMap<>();

            ranks.put("**Global rank**", findUserPosition(globalRankList, userId));

            if (roles.stream().anyMatch(r -> r.contains("JAVA"))) {
                ranks.put("Java rank", findUserPosition(javaRankList, userId));
            }
            if (roles.stream().anyMatch(r -> r.contains("PYTHON"))) {
                ranks.put("Python rank", findUserPosition(pythonRankList, userId));
            }
            if (roles.stream().anyMatch(r -> r.contains("C#"))) {
                ranks.put("C# rank", findUserPosition(cSharpRankList, userId));
            }
            if (roles.stream().anyMatch(r -> r.contains("JAVASCRIPT"))) {
                ranks.put("JavaScript rank", findUserPosition(jsRankList, userId));
            }

            connection.close();

            return ranks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String findUserPosition(Set<String> rankList, String userId) {

        String rank = null;
        int rankCounter = 0;

        for (String currentUserId : rankList) {
            rankCounter++;
            if (currentUserId.equals(userId)) {
                rank = rankCounter + "";
                break;
            }
        }

        return rank != null ? rank : "unranked";
    }

    private void fillGaps(TriviaUserInfo triviaUserInfo) {

        Map<String, Integer> groupPoints = triviaUserInfo.getGroupPoints();
        Map<String, Integer> answeredQuestions = triviaUserInfo.getAnsweredQuestions();
        Map<String, Integer> correctAnswers = triviaUserInfo.getCorrectAnswers();

        if (!groupPoints.containsKey("Java")) {
            groupPoints.put("Java", 0);
            answeredQuestions.put("Java", 0);
            correctAnswers.put("Java", 0);
        }

        if (!groupPoints.containsKey("C#")) {
            groupPoints.put("C#", 0);
            answeredQuestions.put("C#", 0);
            correctAnswers.put("C#", 0);
        }

        if (!groupPoints.containsKey("Python")) {
            groupPoints.put("Python", 0);
            answeredQuestions.put("Python", 0);
            correctAnswers.put("Python", 0);
        }

        if (!groupPoints.containsKey("JS")) {
            groupPoints.put("JS", 0);
            answeredQuestions.put("JS", 0);
            correctAnswers.put("JS", 0);
        }

    }

    private String findGroupDisplayName(String group) {

        return switch (group) {
            case "java" -> "Java";
            case "c#" -> "C#";
            case "js" -> "JS";
            case "python" -> "Python";
            default -> "null";
        };
    }

    public void insertQuestion(List<TriviaQuestionInsertCommand> uniqueQuestions) {

        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.trivia_questions(id, title, points, answer_a, answer_c, answer_b, answer_d, correct_answer, created_on, author, image_url, complexity, `group`)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                    """);

            Random random = new Random();

            for (TriviaQuestionInsertCommand questionCommand : uniqueQuestions) {

                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, questionCommand.getQuestion());
                statement.setInt(3, random.nextInt(5, 16));
                statement.setString(4, questionCommand.getOptions().get("a"));
                statement.setString(5, questionCommand.getOptions().get("c"));
                statement.setString(6, questionCommand.getOptions().get("b"));
                statement.setString(7, questionCommand.getOptions().get("d"));
                statement.setString(8, questionCommand.getCorrect_answer());
                statement.setTimestamp(9, Timestamp.valueOf(OffsetDateTime.now().toLocalDateTime()));
                statement.setString(10, "ceo");
                statement.setString(11, "unknown");
                statement.setString(12, "easy");
                statement.setString(13, "c#");

                statement.addBatch();
            }

            statement.executeBatch();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
