package com.mandarin.discord.listener;

import com.mandarin.discord.command.*;
import com.mandarin.discord.command.status.GuildMemberRemoveToggle;
import com.mandarin.discord.config.GuildStartupConfiguration;
import com.mandarin.discord.json.ReadJsonCommand;
import com.mandarin.discord.scheduler.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class EventListenerRegistry {

    public static final List<ListenerAdapter> EVENT_LISTENERS = List.of(
            //configuration
            new GuildStartupConfiguration(),
            //event listeners
            new GuildMemberRemovedEventListener(),
            //schedulers
            new ExamStatusScheduler(),
            new JavaRankListScheduler(),
            new CSharpRankListScheduler(),
            new JavaScriptRankListScheduler(),
            new PythonRankListScheduler(),
            new GlobalRankListScheduler(),
            //commands
            new TriviaTriggerCommand(),
            new TriviaPointsCheckoutCommand(),
            new TriviaRankListCommand(),
            new LatestUpdateCommand(),
            new GuildMemberRemoveToggle(),
            new ExamInsertCommand(),
            new ExamNotificationCommand(),
            new ExamListCommand(),
            new ChannelLockCommand(),
            new ChannelUnlockCommand(),
            new ChannelSyncCommand(),
            new ReadJsonCommand()
    );
}
