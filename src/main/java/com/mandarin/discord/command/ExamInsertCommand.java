package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.ExamRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class ExamInsertCommand extends ListenerAdapter {

    public static final String EXAM_INSERT_COMMAND_NAME = "exam-insert";
    private final ExamRepository examRepository;

    public ExamInsertCommand() {
        this.examRepository = new ExamRepository();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(
                event,
                EXAM_INSERT_COMMAND_NAME,
                SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                List.of(GuildRole.EVENT_MANAGER, GuildRole.GLOBAL_MODERATOR));

        if (!access) {
            return;
        }

        String courseName = Objects.requireNonNull(event.getOption("course")).getAsString();
        LocalDate startDate = null;
        LocalDate endDate = null;

        try {
            startDate = LocalDate.parse(Objects.requireNonNull(event.getOption("start-date")).getAsString());
            endDate = LocalDate.parse(Objects.requireNonNull(event.getOption("end-date")).getAsString());
        } catch (DateTimeParseException e) {
            event.reply("**Oh!** It seems you are using incorrect date format. Both dates must be in the format **yyyy-MM-dd**. Example: 2023-12-25").queue();
        }

        try {
            Objects.requireNonNull(startDate);
            Objects.requireNonNull(endDate);
        } catch (NullPointerException e) {
            event.reply("**Oh!** I need start and end date for an exam to insert.").queue();
        }

        boolean isFinished = LocalDate.now().isAfter(endDate);

        String dayOfMonthStart = String.valueOf(startDate.getDayOfMonth());
        String ordinalStart = getOrdinalSuffix(Integer.parseInt(dayOfMonthStart));
        String formattedMonthStart = startDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String outputStartDate = ordinalStart + " of " + formattedMonthStart + " " + startDate.getYear();

        String dayOfMonthEnd = String.valueOf(endDate.getDayOfMonth());
        String ordinalEnd = getOrdinalSuffix(Integer.parseInt(dayOfMonthEnd));
        String formattedMonthEnd = endDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String outputEndDate = ordinalEnd + " of " + formattedMonthEnd + " " + endDate.getYear();

        examRepository.insert(courseName, startDate, endDate, isFinished, event.getUser().getName());
        event.reply(String.format("Exam was inserted: **%s**, starts on **%s** and ends on **%s**.", courseName, outputStartDate, outputEndDate)).queue();
    }

    private static String getOrdinalSuffix(int dayOfMonth) {
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return dayOfMonth + "th";
        }
        switch (dayOfMonth % 10) {
            case 1:
                return dayOfMonth + "st";
            case 2:
                return dayOfMonth + "nd";
            case 3:
                return dayOfMonth + "rd";
            default:
                return dayOfMonth + "th";
        }
    }

}
