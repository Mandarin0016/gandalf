package com.mandarin.discord.command.status;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.CommandRepository;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.service.MemberService.isMissingAppropriateRole;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class GuildMemberRemoveToggle extends ListenerAdapter {

    public static final String LOGGING_MEMBER_REMOVAL_COMMAND_NAME = "logging-member-removal";
    private final CommandRepository commandRepository;

    public GuildMemberRemoveToggle() {

        commandRepository = new CommandRepository();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(
                event,
                LOGGING_MEMBER_REMOVAL_COMMAND_NAME,
                SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                List.of(GuildRole.EVENT_MANAGER, GuildRole.GLOBAL_MODERATOR));

        if (!access) {
            return;
        }

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
