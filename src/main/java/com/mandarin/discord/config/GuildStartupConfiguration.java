package com.mandarin.discord.config;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.List;

public class GuildStartupConfiguration extends ListenerAdapter {

    public static String SOFTUNI_PROGRAMMING_BASICS_GUILD_ID = "886268434004983808";

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("trivia-start", "Start a trivia challenge now!"));
        SlashCommandData command = new CommandDataImpl("logging-member-removal", "enable/disable the logging for member removal event occurrence.");
        command.addOption(OptionType.STRING, "status", "The status to move on.", true);
        commandData.add(command);
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
