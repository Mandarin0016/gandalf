package com.mandarin.discord.scheduler;

import com.mandarin.discord.command.TriviaRankListCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;

public class JavaRankListScheduler extends ListenerAdapter {

    private ScheduledExecutorService scheduler;
    private TriviaRankListCommand triviaRankListCommand;

    public JavaRankListScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.triviaRankListCommand = new TriviaRankListCommand();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        event.getJDA()
                .getGuildById(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID)
                .getTextChannelById("1118952359423377469")
                .sendMessage(MessageCreateData.fromEmbeds(triviaRankListCommand.buildGroupRankListEmbed(event, "java"))).queue(sentMessage ->

                        scheduler.scheduleAtFixedRate(() -> {
                            MessageEmbed messageEmbed = triviaRankListCommand.buildGroupRankListEmbed(event, "java");
                            sentMessage.editMessage(MessageEditData.fromEmbeds(messageEmbed)).queue();
                        }, 0, 1, TimeUnit.MINUTES)
                );
    }

}
