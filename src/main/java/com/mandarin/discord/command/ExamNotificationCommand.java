package com.mandarin.discord.command;

import com.mandarin.discord.entity.Exam;
import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.ExamRepository;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.common.CommonMessage.EXAM_NOTIFICATION;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class ExamNotificationCommand extends ListenerAdapter {

    public static final String EXAM_NOTIFICATION_COMMAND_NAME = "exam-notify";

    private final ExamRepository examRepository;

    public ExamNotificationCommand() {
        this.examRepository = new ExamRepository();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(
                event,
                EXAM_NOTIFICATION_COMMAND_NAME,
                SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                List.of(GuildRole.EVENT_MANAGER, GuildRole.GLOBAL_MODERATOR));

        if (!access) {
            return;
        }

        examRepository.updateExamStatus();
        Exam upcomingExam = examRepository.findValidUpcomingExam();

        String monthNameInBulgarian = getMonthNameInBulgarian(upcomingExam.getStartDate().getMonth().getValue());

        RichCustomEmoji softuniEmoji = event.getGuild().getEmojisByName("softuni", false).get(0);
        RichCustomEmoji alertEmoji = event.getGuild().getEmojisByName("alert", true).get(0);

        String completeMessage = String.format(EXAM_NOTIFICATION,
                alertEmoji.getAsMention(),
                upcomingExam.getStartDate().getDayOfMonth(),
                upcomingExam.getEndDate().getDayOfMonth(),
                monthNameInBulgarian,
                upcomingExam.getCourseName(),
                upcomingExam.getStartDate().getDayOfMonth(),
                monthNameInBulgarian,
                upcomingExam.getEndDate().getDayOfMonth(),
                monthNameInBulgarian,
                softuniEmoji.getAsMention()
        );

        Objects.requireNonNull(event.getJDA().getTextChannelById("886603663961907261")).sendMessage(completeMessage).queue();
        event.reply("I've sent the message, please check <#886603663961907261> for it.").queue();
    }

    private String getMonthNameInBulgarian(int value) {
        return switch (value) {
            case 1 -> "Януари";
            case 2 -> "Февруари";
            case 3 -> "Март";
            case 4 -> "Април";
            case 5 -> "Май";
            case 6 -> "Юни";
            case 7 -> "Юли";
            case 8 -> "Август";
            case 9 -> "Септември";
            case 10 -> "Октомври";
            case 11 -> "Ноември";
            default -> "Декември";
        };
    }

}
