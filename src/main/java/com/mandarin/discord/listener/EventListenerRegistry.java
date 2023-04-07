package com.mandarin.discord.listener;

import com.mandarin.discord.command.CommandManager;
import com.mandarin.discord.command.TriviaTriggerCommand;
import com.mandarin.discord.config.GuildStartupConfiguration;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class EventListenerRegistry {

    public static final List<ListenerAdapter> EVENT_LISTENERS = List.of(
            new CommandManager(),
            new GuildStartupConfiguration(),
            new GuildMemberRemovedEventListener(),
            new TriviaTriggerCommand()
    );
}
