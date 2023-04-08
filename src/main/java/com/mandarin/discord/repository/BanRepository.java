package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class BanRepository {

    public void insert(User bannedUser, User banner, String reason, String guildId) {

        try {
            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO gandalf.banns(`user_id`, `user_tag`, `banner_id`, `banner_tag`, `datetime`, `guild_id`, `reason`)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """);

            statement.setString(1, bannedUser.getId());
            statement.setString(2, bannedUser.getName());
            statement.setString(3, banner.getId());
            statement.setString(4, banner.getName());
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
