package com.mandarin.discord.command;

import com.mandarin.discord.config.JdbcConnection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
//        String command = event.getName();
//        if (command.equalsIgnoreCase("welcome")) {
//
//            try {
//                Connection connection = JdbcConnection.getConnection();
//                insertUserRecord(connection, event);
//                connection.close();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//
//            String userTag = event.getUser().getName();
//            event.reply("Welcome to the server **" + userTag + "**! I also added you to my database!").queue();
//        }
    }

    private void insertUserRecord(Connection connection, SlashCommandInteractionEvent event) throws SQLException {

        PreparedStatement statement;

        statement = connection.prepareStatement("""
                INSERT INTO users(user_id, username, datetime)
                VALUES (?, ?, ?);
                """);

        statement.setString(1, event.getUser().getId());
        statement.setString(2, event.getUser().getName());
        statement.setTimestamp(3, Timestamp.from(Instant.now()));

        statement.executeUpdate();
    }
}
