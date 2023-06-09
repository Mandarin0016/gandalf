package com.mandarin.discord.command;

import com.mandarin.discord.entity.Exam;
import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.ExamRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;
import static com.mandarin.discord.util.ServerInitiator.findServerInitiator;

public class ExamListCommand extends ListenerAdapter {

    public static final String EXAM_LIST_COMMAND_NAME = "exam-list";

    private final ExamRepository examRepository;

    public ExamListCommand() {
        this.examRepository = new ExamRepository();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(
                event,
                EXAM_LIST_COMMAND_NAME,
                List.of(
                        SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                        SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID),
                List.of(
                        GuildRole.EVENT_MANAGER_BASICS,
                        GuildRole.GLOBAL_MODERATOR_BASICS,
                        GuildRole.EVENT_MANAGER_FUNDAMENTALS,
                        GuildRole.GLOBAL_MODERATOR_FUNDAMENTALS));

        if (!access) {
            return;
        }

        examRepository.updateExamStatus();
        List<Exam> upcomingExams = examRepository.findAllReady(findServerInitiator(event));

        StringBuilder sb = new StringBuilder();
        sb.append("These are the upcoming exams: ").append(System.lineSeparator());

        for (Exam upcomingExam : upcomingExams) {
            sb.append("Course name: ")
                    .append(String.format("**%s**", upcomingExam.getCourseName()))
                    .append(" | Starts on ")
                    .append(String.format("**%s**", upcomingExam.getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"))))
                    .append(" | Ends on ")
                    .append(String.format("**%s**", upcomingExam.getEndDate().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"))))
                    .append(".")
                    .append(" *(този изпит беше добавен от ")
                    .append(upcomingExam.getCreatorUsername())
                    .append(")*")
                    .append(System.lineSeparator());
        }

        event.reply(sb.toString()).queue();
    }
}
