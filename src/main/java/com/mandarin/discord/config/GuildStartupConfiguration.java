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

import static com.mandarin.discord.config.ApplicationConfiguration.isProdLoaded;

public class GuildStartupConfiguration extends ListenerAdapter {

    public static String SOFTUNI_PROGRAMMING_BASICS_GUILD_ID = "886268434004983808";
    public static String SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID = "954298970799243285";
    public static String TRIVIA_TEST_GUILD_ID = "1092768978285367316";

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        SlashCommandData triviaRankList = new CommandDataImpl("rank-list", "Retrieve rank-list with the given flag: global, java, c#, python, js");
        triviaRankList.addOption(OptionType.STRING, "flag", "global, java, c#, python, js", true);
        SlashCommandData latestUpdateCommand = new CommandDataImpl("latest-update-notify", "Notify the latest update.");
        latestUpdateCommand.addOption(OptionType.STRING, "version", "The version for which the update notification will be.");
        latestUpdateCommand.addOption(OptionType.STRING, "details", "Any additional details, if you have.");
        SlashCommandData triviaStartCommand = new CommandDataImpl("trivia-start", "Start a trivia challenge now!");
        triviaStartCommand.addOption(OptionType.STRING, "group", "The group you start the trivia for. Possibility: java, js, python or c#", true);
        triviaStartCommand.addOption(OptionType.STRING, "complexity", "The complexity of the questions. Possibility: easy, medium or hard", true);
        triviaStartCommand.addOption(OptionType.INTEGER, "count", "Hay many questions you want that trivia to has. Possibility: 1-10", true);
        commandData.add(triviaStartCommand);
        commandData.add(latestUpdateCommand);
        commandData.add(triviaRankList);
        if (isProdLoaded()) {
            SlashCommandData triviaMyPoints = new CommandDataImpl("points", "Check your trivia points.");
            commandData.add(triviaMyPoints);
        }
        SlashCommandData jsonRead = new CommandDataImpl("json-read", "Only for internal use.");
        jsonRead.addOption(OptionType.STRING, "lang", "lang: js, c#, js, python");
        jsonRead.addOption(OptionType.STRING, "version", "version: 1+");
        commandData.add(jsonRead);
        SlashCommandData loggingMemberRemovalCommand = new CommandDataImpl("logging-member-removal", "Enable/Disable the logging for member removal event occurrence.");
        loggingMemberRemovalCommand.addOption(OptionType.STRING, "status", "The status to move on.", true);
        commandData.add(loggingMemberRemovalCommand);
        SlashCommandData examNotifyCommand = new CommandDataImpl("exam-notify", "Notify the students about an upcoming exam and channel lockdown.");
        commandData.add(examNotifyCommand);
        SlashCommandData examInsertCommand = new CommandDataImpl("exam-insert", "Insert an upcoming exam.");
        examInsertCommand.addOption(OptionType.STRING, "course", "The course that will perform the exam. Example: Programming Basics - 22 Април 2023", true);
        examInsertCommand.addOption(OptionType.STRING, "start-date", "The first day of the exam, should be Saturday. Example: 10-06-2023", true);
        examInsertCommand.addOption(OptionType.STRING, "end-date", "The end day of the exam, should be Sunday. Example: 11-06-2023", true);
        commandData.add(examInsertCommand);
        commandData.add(Commands.slash("exam-list", "List all valid upcoming exams."));
        SlashCommandData channelsLockCommand = new CommandDataImpl("lock", "Lock all channels to ensure the normal conduct of the exam.");
        commandData.add(channelsLockCommand);
        SlashCommandData channelsUnlockCommand = new CommandDataImpl("unlock", "Unlock all channels.");
        commandData.add(channelsUnlockCommand);
        SlashCommandData channelsSyncCommand = new CommandDataImpl("sync", "Sync all channels under every category.");
        commandData.add(channelsSyncCommand);
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
