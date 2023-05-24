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
    public static String SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID = "954298970799243285";

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("trivia-start", "Start a trivia challenge now!"));
        SlashCommandData loggingMemberRemovalCommand = new CommandDataImpl("logging-member-removal", "Enable/Disable the logging for member removal event occurrence.");
        loggingMemberRemovalCommand.addOption(OptionType.STRING, "status", "The status to move on.", true);
        commandData.add(loggingMemberRemovalCommand);
        SlashCommandData examNotifyCommand = new CommandDataImpl("exam-notify", "Notify the students about an upcoming exam and channel lockdown.");
        commandData.add(examNotifyCommand);
        SlashCommandData examInsertCommand = new CommandDataImpl("exam-insert", "Insert an upcoming exam.");
        examInsertCommand.addOption(OptionType.STRING, "course", "The course that will perform the exam. Example: Programming Basics - 22 Април 2023", true);
        examInsertCommand.addOption(OptionType.STRING, "start-date", "The first day of the exam, should be Saturday. Example: 2023-06-10", true);
        examInsertCommand.addOption(OptionType.STRING, "end-date", "The end day of the exam, should be Sunday. Example: 2023-06-11", true);
        commandData.add(examInsertCommand);
        commandData.add(Commands.slash("exam-list", "List all valid upcoming Programming Basics exams."));
        SlashCommandData channelsLockCommand = new CommandDataImpl("lock", "Lock all channels to ensure the normal conduct of the exam.");
        commandData.add(channelsLockCommand);
        SlashCommandData channelsUnlockCommand = new CommandDataImpl("unlock", "Unlock all channels.");
        commandData.add(channelsUnlockCommand);
        SlashCommandData channelsSyncCommand = new CommandDataImpl("sync", "Sync all channels under every category.");
        commandData.add(channelsSyncCommand);
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
