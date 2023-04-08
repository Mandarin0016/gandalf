package com.mandarin.discord.command.status;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.CommandRepository;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;

public class GuildMemberRemoveToggle extends ListenerAdapter {

    private final CommandRepository commandRepository;

    public GuildMemberRemoveToggle() {
        commandRepository = new CommandRepository();
    }

    public static final String LOGGING_MEMBER_REMOVAL_COMMAND_NAME = "logging-member-removal";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(LOGGING_MEMBER_REMOVAL_COMMAND_NAME)) {
            return;
        }
        if (!event.getGuild().getId().equals(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID)) {
            event.reply("Not enable for your server!").setEphemeral(true).queue();
            return;
        }

        boolean isMissingAppropriateRole = event.getMember()
                .getRoles()
                .stream()
                .map(Role::getId)
                .noneMatch(rid -> GuildRole.GLOBAL_MODERATOR.getRoleId().equals(rid) ||
                        GuildRole.EVENT_MANAGER.getRoleId().equals(rid));

        if (isMissingAppropriateRole) {
            event.reply("You don't have access to perform this action. Please contact a member that has <@&886273306028834816> role and ask for assistance.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();
        OptionMapping option = Objects.requireNonNull(event.getOption("status"));

        if (option.getAsString().equalsIgnoreCase("disable") || option.getAsString().equalsIgnoreCase("enable")) {

            String newStatus = option.getAsString().equalsIgnoreCase("enable") ? "enabled" : "disabled";
            commandRepository.updateStatus(LOGGING_MEMBER_REMOVAL_COMMAND_NAME, newStatus, event.getUser().getName());
            event.reply(String.format("logging-member-removal is now %s.", newStatus)).queue();
        } else {

            event.reply("Incorrect status value. It could be either **disable** or **enable**.").setEphemeral(true).queue();
        }

    }
}
