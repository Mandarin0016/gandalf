package com.mandarin.discord.listener;

import com.mandarin.discord.config.ApplicationConfiguration;
import com.mandarin.discord.config.JdbcConnection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

public class GuildMemberRemovedEventListener extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        if (!event.getGuild().getId().equals("886268434004983808")) {
            return;
        }

        Member member = event.getMember();
        String user = member.getAsMention();
        Guild guild = event.getGuild();

        if (guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            guild.retrieveAuditLogs().queue(logs -> {
                AuditLogEntry entry = logs.stream().filter(logEntry ->
                        logEntry.getTargetId().equals(member.getId()) &&
                                logEntry.getTimeCreated().isAfter(OffsetDateTime.now().minusSeconds(5))
                ).findFirst().orElse(null);
                if (entry != null && entry.getType() == ActionType.KICK) {

                    String kickerName = entry.getUser().getAsMention();
                    String reason = entry.getReason();
                    TextChannel channel = guild.getTextChannelById("1093558267382345778");
                    Objects.requireNonNull(channel);
                    channel.sendMessage(user + " has been kicked from the server by " + kickerName + ". Reason: " + reason).queue();

                    try {
                        insertKickRecord(member, entry.getUser(), reason, guild.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else if (entry != null && entry.getType() == ActionType.BAN) {

                    String banner = entry.getUser().getAsMention();
                    String reason = entry.getReason();
                    TextChannel channel = guild.getTextChannelById("1093558651106631772");
                    Objects.requireNonNull(channel);
                    channel.sendMessage(user + " has been banned from the server by " + banner + ". Reason: " + reason).queue();
                } else {

                    TextChannel channel = guild.getTextChannelById("1093558615337615441");
                    Objects.requireNonNull(channel);
                    channel.sendMessage(user + " has left the server.").queue();
                }
            });
        }

    }

    private void insertKickRecord(Member user, User kicker, String reason, String guildId) throws SQLException {

        Connection connection = JdbcConnection.getConnection();

        String query = ApplicationConfiguration.getDefaultInternalEnvConfig().get("INSERT_KICK_RECORD_QUERY");
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, user.getId());
        statement.setString(2, user.getEffectiveName());
        statement.setString(3, kicker.getId());
        statement.setString(4, kicker.getName());
        statement.setTimestamp(5, Timestamp.from(Instant.now()));
        statement.setString(6, guildId);
        statement.setString(7, reason);

        statement.executeUpdate();

        connection.close();
    }
}
