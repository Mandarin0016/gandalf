package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.ExamRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;
import static com.mandarin.discord.util.ServerInitiator.findServerInitiator;

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

        String courseName = Objects.requireNonNull(event.getOption("course")).getAsString();
        LocalDate startDate = null;
        LocalDate endDate = null;
        String server = findServerInitiator(event);

        try {

            String startDateAsString = Objects.requireNonNull(event.getOption("start-date")).getAsString();
            String endDateAsString = Objects.requireNonNull(event.getOption("end-date")).getAsString();

            startDate = LocalDate.parse(startDateAsString, DateTimeFormatter.ofPattern("dd-MM-uuuu"));
            endDate = LocalDate.parse(endDateAsString, DateTimeFormatter.ofPattern("dd-MM-uuuu"));

        } catch (DateTimeParseException e) {
            event.reply("**Oh!** It seems you are using incorrect date format. Both dates must be in the format **dd-MM-yyyy**. Example: 25-12-2023").queue();
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

        examRepository.insert(courseName, startDate, endDate, isFinished, event.getUser().getName(), server);
        event.reply(String.format("Exam was inserted: **%s**, starts on **%s** and ends on **%s**.", courseName, outputStartDate, outputEndDate)).queue();
    }

    private static String getOrdinalSuffix(int dayOfMonth) {

        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            return dayOfMonth + "th";
        }

        return switch (dayOfMonth % 10) {
            case 1 -> dayOfMonth + "st";
            case 2 -> dayOfMonth + "nd";
            case 3 -> dayOfMonth + "rd";
            default -> dayOfMonth + "th";
        };
    }

}
