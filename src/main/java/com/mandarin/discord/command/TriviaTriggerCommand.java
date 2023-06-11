package com.mandarin.discord.command;

import com.mandarin.discord.entity.TriviaAnswer;
import com.mandarin.discord.entity.TriviaQuestion;
import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.*;
import static com.mandarin.discord.util.ButtonIdentifier.TRIVIA_ANSWER_BUTTON;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyButtonAccessToEventListener;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class TriviaTriggerCommand extends ListenerAdapter {

    public static final String TRIVIA_START_COMMAND_NAME = "trivia-start";
    public static final String JAVA_GROUP = "java";
    public static final String CSHARP_GROUP = "c#";
    public static final String JS_GROUP = "js";
    public static final String PYTHON_GROUP = "python";
    public final TriviaRepository triviaRepository;

    public TriviaTriggerCommand() {
        triviaRepository = new TriviaRepository();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(TRIVIA_START_COMMAND_NAME)) {
            return;
        }

        boolean access = verifyCommandAccess(
                event,
                TRIVIA_START_COMMAND_NAME,
                java.util.List.of(
                        SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                        SOFTUNI_PROGRAMMING_FUNDAMENTALS_GUILD_ID,
                        TRIVIA_TEST_GUILD_ID),
                List.of(
                        GuildRole.EVENT_MANAGER_BASICS,
                        GuildRole.GLOBAL_MODERATOR_BASICS,
                        GuildRole.EVENT_MANAGER_FUNDAMENTALS,
                        GuildRole.GLOBAL_MODERATOR_FUNDAMENTALS,
                        GuildRole.TESTER_TRIVIA_SERVER_ROLE));

        if (!access) {
            return;
        }

        Guild guild = event.getGuild();

        String group = Objects.requireNonNull(event.getOption("group")).getAsString();
        String complexity = Objects.requireNonNull(event.getOption("complexity")).getAsString().toUpperCase();

        if (!isValidGroup(group) || !isValidComplexity(complexity)) {
            event.reply("Invalid data provided! Check carefully the group and the complexity!").setEphemeral(true).queue();
            return;
        }

        TriviaQuestion triviaQuestion = triviaRepository.findRandomQuestion(group, TriviaQuestion.Complexity.valueOf(complexity));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(triviaQuestion.getTitle());
        builder.setColor(findComplexityColor(triviaQuestion.getComplexity()));
        builder.setThumbnail("https://www.cumberlandforest.com/backwoods/wp-content/uploads/2021/03/trivia-night.jpg");
        builder.setDescription("Точки: " + triviaQuestion.getPoints());
        builder.addField("А:", triviaQuestion.getAnswerA(), false);
        builder.addField("Б:", triviaQuestion.getAnswerB(), false);
        builder.addField("В:", triviaQuestion.getAnswerC(), false);
        builder.addField("Г:", triviaQuestion.getAnswerD(), false);
        builder.setFooter("Група: " + triviaQuestion.getGroup() + " | Сложност: " + findComplexityDisplayName(triviaQuestion.getComplexity()), findFooterIcon(group));
        builder.setTimestamp(Instant.now());

        MessageEmbed embed = builder.build();

        Button buttonA = Button.success(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "A$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "A");
        Button buttonB = Button.success(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "B$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "Б");
        Button buttonC = Button.success(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "C$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "В");
        Button buttonD = Button.success(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "D$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "Г");

        guild.getTextChannelById(findAppropriateChannel(guild.getId(), event.getOption("group").getAsString()))
                .sendMessageEmbeds(embed)
                .setActionRow(buttonA, buttonB, buttonC, buttonD)
                .queue();

        event.reply("Trivia has started!").setEphemeral(true).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (!verifyButtonAccessToEventListener(event, TRIVIA_ANSWER_BUTTON)) {
            return;
        }

        String buttonId = event.getButton().getId();
        String userId = event.getMember().getId();
        String questionId = buttonId.split("\\$")[0];

        if (triviaRepository.doesUserHasAnswer(userId, questionId)) {
            TriviaAnswer answer = triviaRepository.getUserAnswer(userId, questionId);
            event.reply("Не може да отговаряте повер от веднъж! Отговора, който вече изпратихте е: \"" + answer.getUserAnswer().toUpperCase() + "\" | Изпратен на: " + answer.getCreatedOn().toString()).setEphemeral(true).queue();
            return;
        }

        String buttonLabel = event.getButton().getLabel().toLowerCase();
        String correctAnswer = buttonId.split("\\$")[3];
        int points = Integer.parseInt(buttonId.split("\\$")[4]);

        if (buttonLabel.equalsIgnoreCase(correctAnswer)) {
            triviaRepository.insertAnswer(userId, buttonId, buttonLabel, true, points);
        } else {
            triviaRepository.insertAnswer(userId, buttonId, buttonLabel, false, 0);
        }

        event.reply("Вие отговорихте успешно на въпроса!").setEphemeral(true).queue();
    }

    private String findFooterIcon(String group) {

        return switch (group) {
            case JAVA_GROUP -> "https://cdn-icons-png.flaticon.com/512/5968/5968282.png";
            case CSHARP_GROUP -> "https://cdn-icons-png.flaticon.com/512/6132/6132221.png";
            case PYTHON_GROUP -> "https://icons.iconarchive.com/icons/cornmanthe3rd/plex/512/Other-python-icon.png";
            case JS_GROUP ->
                    "https://www.freepnglogos.com/uploads/javascript-png/javascript-vector-logo-yellow-png-transparent-javascript-vector-12.png";
            default -> "";
        };
    }

    private Color findComplexityColor(TriviaQuestion.Complexity complexity) {

        return switch (complexity) {
            case EASY -> Color.GREEN;
            case NORMAL -> Color.BLUE;
            case HARD -> Color.RED;
        };
    }

    private boolean isValidGroup(String group) {

        return switch (group) {
            case JAVA_GROUP, JS_GROUP, CSHARP_GROUP, PYTHON_GROUP -> true;
            default -> false;
        };
    }

    private boolean isValidComplexity(String complexity) {

        return switch (complexity) {
            case "EASY", "NORMAL", "HARD" -> true;
            default -> false;
        };
    }

    private String findAppropriateChannel(String id, String group) {

        if (SOFTUNI_PROGRAMMING_BASICS_GUILD_ID.equals(id)) {

            return switch (group) {
                case JAVA_GROUP -> "938173322963845130";
                case CSHARP_GROUP -> "938173469764501554";
                case PYTHON_GROUP -> "938173606037430392";
                case JS_GROUP -> "938173738418073692";
                default -> "";
            };
        } else if (TRIVIA_TEST_GUILD_ID.equals(id)) {

            // test channel in test trivia server
            return "1093637377307713636";
        } else {

            //it only supports Programming Basics and test trivia server for now
            return SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
        }
    }

    private String findComplexityDisplayName(TriviaQuestion.Complexity complexity) {

        return switch (complexity) {
            case EASY -> "Лесна";
            case NORMAL -> "Нормална";
            case HARD -> "Трудна";
        };
    }
}
