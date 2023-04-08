package com.mandarin.discord.listener;

import com.mandarin.discord.command.TriviaTriggerCommand;
import com.mandarin.discord.command.status.GuildMemberRemoveToggle;
import com.mandarin.discord.config.GuildStartupConfiguration;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class EventListenerRegistry {

    public static final List<ListenerAdapter> EVENT_LISTENERS = List.of(
            //configuration
            new GuildStartupConfiguration(),
            //event listeners
            new GuildMemberRemovedEventListener(),
            //commands
            new TriviaTriggerCommand(),
            new GuildMemberRemoveToggle()
    );
}
