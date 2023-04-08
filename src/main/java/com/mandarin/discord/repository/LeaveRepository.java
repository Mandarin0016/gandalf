package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class LeaveRepository {

    public void insert(User leftUser, String guildId) {
        try {

            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.leaves(`user_id`, `user_tag`, `datetime`, `guild_id`)
                    VALUES (?,?,?,?);
                    """);

            statement.setString(1, leftUser.getId());
            statement.setString(2, leftUser.getName());
            statement.setTimestamp(3, Timestamp.from(Instant.now()));
            statement.setString(4, guildId);

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
