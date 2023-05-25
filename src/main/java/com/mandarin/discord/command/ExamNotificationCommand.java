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

import static com.mandarin.discord.common.CommonMessage.EXAM_NOTIFICATION_BASICS;
import static com.mandarin.discord.common.CommonMessage.EXAM_NOTIFICATION_FUNDAMENTALS;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;
import static com.mandarin.discord.util.ServerInitiator.findAnnouncementChannelId;
import static com.mandarin.discord.util.ServerInitiator.findServerInitiator;

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
        String server = findServerInitiator(event);
        Exam upcomingExam = examRepository.findValidUpcomingExam(server);

        String monthNameInBulgarian = getMonthNameInBulgarian(upcomingExam.getStartDate().getMonth().getValue());

        RichCustomEmoji softuniEmoji = event.getGuild().getEmojisByName("softuni", false).get(0);
        RichCustomEmoji alertEmoji = event.getGuild().getEmojisByName("alert", true).get(0);

        String completeMessage;

        if(server.equals("BASICS")) {
            completeMessage = getBasicsMessage(upcomingExam, monthNameInBulgarian, softuniEmoji, alertEmoji);
        } else {
            completeMessage = getFundamentalsMessage(upcomingExam, monthNameInBulgarian, softuniEmoji, alertEmoji);
        }


        Objects.requireNonNull(event.getJDA().getTextChannelById(findAnnouncementChannelId(server))).sendMessage(completeMessage).queue();
        event.reply(String.format("I've sent the message, please check <#%s> for it.", findAnnouncementChannelId(server))).queue();
    }

    private String getBasicsMessage(Exam upcomingExam, String monthNameInBulgarian, RichCustomEmoji softuniEmoji, RichCustomEmoji alertEmoji) {

        return String.format(EXAM_NOTIFICATION_BASICS,
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
    }
    private String getFundamentalsMessage(Exam upcomingExam, String monthNameInBulgarian, RichCustomEmoji softuniEmoji, RichCustomEmoji alertEmoji) {

        return String.format(EXAM_NOTIFICATION_FUNDAMENTALS,
                alertEmoji.getAsMention(),
                upcomingExam.getStartDate().getDayOfMonth(),
                monthNameInBulgarian,
                upcomingExam.getCourseName(),
                upcomingExam.getStartDate().getDayOfMonth(),
                monthNameInBulgarian,
                softuniEmoji.getAsMention()
        );
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
