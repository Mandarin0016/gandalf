package com.mandarin.discord.repository;

import com.mandarin.discord.config.JdbcConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;


public class UpdateRepository {

    public Map<String, String> findUpdateEntry(String version) {

        try {

            Connection connection = JdbcConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * 
                    FROM `gandalf`.`updates`
                    WHERE version = ?
                    """);

            statement.setString(1, version);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            Map<String, String> updateEntry = new HashMap<>();

            updateEntry.put("version", resultSet.getString("version"));
            updateEntry.put("new", resultSet.getString("new"));
            updateEntry.put("change", resultSet.getString("change"));
            updateEntry.put("remove", resultSet.getString("remove"));
            updateEntry.put("createdOn", resultSet.getTimestamp("created_on").toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
            updateEntry.put("developers", resultSet.getString("developers"));

            connection.close();
            return updateEntry;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
