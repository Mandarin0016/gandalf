package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class KickRepository {

    public void insert(User kickedUser, User kicker, String reason, String guildId) {

        try {

            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.kicks(`user_id`, `user_tag`, `kicker_id`, `kicker_tag`, `datetime`, `guild_id`, `reason`)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """);

            statement.setString(1, kickedUser.getId());
            statement.setString(2, kickedUser.getName());
            statement.setString(3, kicker.getId());
            statement.setString(4, kicker.getName());
            statement.setTimestamp(5, Timestamp.from(Instant.now()));
            statement.setString(6, guildId);
            statement.setString(7, reason);

            statement.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
