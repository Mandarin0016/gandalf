package com.mandarin.discord.scheduler;

import com.mandarin.discord.repository.ExamRepository;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExamStatusScheduler extends ListenerAdapter {
    private ScheduledExecutorService scheduler;
    private final ExamRepository examRepository;

    public ExamStatusScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.examRepository = new ExamRepository();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        scheduler.scheduleAtFixedRate(() -> {

            examRepository.updateExamStatus();
            event.getJDA().getTextChannelById("1102305243284385852").sendMessage("I've just updated the exam dates and status.").queue();
        }, 0, 24, TimeUnit.HOURS);
    }
}
