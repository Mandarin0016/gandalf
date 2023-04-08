package com.mandarin.discord.listener;

import com.mandarin.discord.repository.CommandRepository;
import com.mandarin.discord.repository.BanRepository;
import com.mandarin.discord.repository.KickRepository;
import com.mandarin.discord.repository.LeaveRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.mandarin.discord.command.status.GuildMemberRemoveToggle.LOGGING_MEMBER_REMOVAL_COMMAND_NAME;

public class GuildMemberRemovedEventListener extends ListenerAdapter {

    private final CommandRepository commandRepository;
    private final BanRepository banRepository;
    private final KickRepository kickRepository;
    private final LeaveRepository leaveRepository;

    public GuildMemberRemovedEventListener() {
        commandRepository = new CommandRepository();
        banRepository = new BanRepository();
        kickRepository = new KickRepository();
        leaveRepository = new LeaveRepository();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        if (!event.getGuild().getId().equals("886268434004983808")) {
            return;
        }

        if (commandRepository.isCommandDisabled(LOGGING_MEMBER_REMOVAL_COMMAND_NAME)) {
            return;
        }

        User userTriggeredEvent = event.getUser();
        Guild guild = event.getGuild();

        if (guild.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {

            guild.retrieveAuditLogs().queue(logs -> {

                AuditLogEntry entry = logs.stream().filter(logEntry ->
                        logEntry.getTargetId().equals(userTriggeredEvent.getId()) &&
                                logEntry.getTimeCreated().isAfter(OffsetDateTime.now().minusSeconds(5))
                ).findFirst().orElse(null);

                if (entry != null && entry.getType() == ActionType.KICK) {

                    User kicker = entry.getUser();
                    String reason = entry.getReason();
                    TextChannel channel = guild.getTextChannelById("1093558267382345778");
                    Objects.requireNonNull(channel);
                    channel.sendMessage("**" +
                            userTriggeredEvent.getName() +
                            "** (" + userTriggeredEvent.getId() + ") has been kicked from the server by " + kicker.getAsMention() +
                            ". Reason: " + reason).queue();

                    kickRepository.insert(userTriggeredEvent, kicker, reason, guild.getId());
                } else if (entry != null && entry.getType() == ActionType.BAN) {

                    User banner = entry.getUser();
                    String reason = entry.getReason();
                    TextChannel channel = guild.getTextChannelById("1093558651106631772");
                    Objects.requireNonNull(channel);
                    channel.sendMessage("**" +
                            userTriggeredEvent.getName() +
                            "** (" + userTriggeredEvent.getId() + ") has been banned from the server by " + banner.getAsMention() +
                            ". Reason: " + reason).queue();

                    banRepository.insert(userTriggeredEvent, banner, reason, guild.getId());
                } else {

                    TextChannel channel = guild.getTextChannelById("1093558615337615441");
                    Objects.requireNonNull(channel);
                    channel.sendMessage("**" +
                            userTriggeredEvent.getName() +
                            "** has left the server. " +
                            "User ID: **" + userTriggeredEvent.getId() + "**").queue();

                    leaveRepository.insert(userTriggeredEvent, guild.getId());
                }
            });
        }

    }


}
