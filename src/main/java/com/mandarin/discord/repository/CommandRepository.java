package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import com.mandarin.discord.exception.InvalidCommandStatusException;

import java.sql.*;
import java.time.Instant;

public class CommandRepository {

    public boolean isCommandDisabled(String commandName) {

        boolean flag = false;

        try (Connection connection = JdbcConnection.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM gandalf.status_commands
                    WHERE command_name = ?
                    """);

            statement.setString(1, commandName);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                String status = resultSet.getString("status");
                if (status.equalsIgnoreCase("enabled")) {
                    flag = false;
                } else if (status.equalsIgnoreCase("disabled")) {
                    flag = true;
                } else {
                    connection.close();
                    throw new InvalidCommandStatusException();
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }

        return flag;
    }

    public void updateStatus(String commandName, String newStatus, String updatedBy) {
        try {

            Connection connection = JdbcConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.`status_commands`
                    (`command_name`, `status`, `last_change_at`, `last_change_by`)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE `status`= ?,
                                            `last_change_at`= ?,
                                            `last_change_by` = ?;
                    """);

            Timestamp now = Timestamp.from(Instant.now());

            statement.setString(1, commandName);
            statement.setString(2, newStatus);
            statement.setTimestamp(3, now);
            statement.setString(4, updatedBy);
            statement.setString(5, newStatus);
            statement.setTimestamp(6, now);
            statement.setString(7, updatedBy);

            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
