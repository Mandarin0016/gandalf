package com.mandarin.discord.command;

import com.mandarin.discord.entity.TriviaAnswer;
import com.mandarin.discord.entity.TriviaQuestion;
import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.TriviaRepository;
import com.mandarin.discord.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
                        TRIVIA_TEST_GUILD_ID),
                List.of(
                        GuildRole.EVENT_MANAGER_BASICS,
                        GuildRole.GLOBAL_MODERATOR_BASICS,
                        GuildRole.TESTER_TRIVIA_SERVER_ROLE));

        if (!access) {
            return;
        }

        Guild guild = event.getGuild();

        String group = Objects.requireNonNull(event.getOption("group")).getAsString();
        String complexity = Objects.requireNonNull(event.getOption("complexity")).getAsString().toUpperCase();
        int count = Objects.requireNonNull(event.getOption("count")).getAsInt();

        if (!isValidGroup(group) || !isValidComplexity(complexity) || !isValidCount(count)) {
            event.reply("Invalid data provided! Check carefully the group and the complexity!").setEphemeral(true).queue();
            return;
        }

        event.reply(MessageCreateData.fromEmbeds(MessageUtil.generateSuccessResponseEmbed("Trivia" + " for **" + group.toUpperCase() + "** " + "has scheduled successfully and starting in **30** seconds! \n Number of questions: **" + count + "** \n Complexity: **" + complexity.toUpperCase() + "** \nQuestion generation starting soon!", event.getMember().getEffectiveName()))).queue();

        triggerNewTrivia(event, guild, group, complexity, count);
    }

    private boolean isValidCount(int count) {
        return count >= 1 && count <= 10;
    }

    private void triggerNewTrivia(SlashCommandInteractionEvent event, Guild guild, String group, String complexity, int questionsCount) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        AtomicInteger messageCounter = new AtomicInteger(1);
        //  AtomicReference<String> messageId = new AtomicReference<>();
        String channelId = findAppropriateChannel(guild.getId(), event.getOption("group").getAsString());

        scheduler.scheduleAtFixedRate(() -> {

            if (messageCounter.get() > questionsCount) {
                //  guild.getTextChannelById(channelId).deleteMessageById(messageId.get()).queue();
                event.getChannel().sendMessage(MessageCreateData.fromEmbeds(MessageUtil.generateSuccessResponseEmbed("All questions were sent successfully for given flags **" + group.toUpperCase() + "**, **" + complexity.toUpperCase() + "**", event.getMember().getEffectiveName()))).queue();
                scheduler.shutdown();
                return;
            }

            //  if (messageId.get() != null) {
            //      guild.getTextChannelById(channelId).deleteMessageById(messageId.get()).queue();
            //  }

            TriviaQuestion triviaQuestion = triviaRepository.findRandomQuestion(group, TriviaQuestion.Complexity.valueOf(complexity));

            if (triviaQuestion.getTitle().equals("MISSING_QUESTION_TITLE")) {
                event.getChannel().sendMessage(MessageCreateData.fromEmbeds(MessageUtil.generateFailureResponseEmbed("No more questions available in the Database with flags **" + group.toUpperCase() + "**, **" + complexity.toUpperCase() + "**", event.getMember().getEffectiveName()))).queue();
                scheduler.shutdown();
                return;
            } else {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle(triviaQuestion.getTitle());
                builder.setColor(findComplexityColor(triviaQuestion.getComplexity()));
                builder.setThumbnail(findThumbnailUrl(messageCounter.get(), complexity));
                builder.setDescription("Точки: **" + triviaQuestion.getPoints() + "**");
                builder.addField("А:", triviaQuestion.getAnswerA(), false);
                builder.addField("B:", triviaQuestion.getAnswerB(), false);
                builder.addField("C:", triviaQuestion.getAnswerC(), false);
                builder.addField("D:", triviaQuestion.getAnswerD(), false);
                builder.setFooter("Група: " + triviaQuestion.getGroup() + " | Сложност: " + findComplexityDisplayName(triviaQuestion.getComplexity()), findFooterIcon(group));
                builder.setTimestamp(Instant.now());

                MessageEmbed embed = builder.build();

                Button buttonA = Button.primary(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "A$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "A");
                Button buttonB = Button.primary(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "B$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "B");
                Button buttonC = Button.primary(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "C$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "C");
                Button buttonD = Button.primary(triviaQuestion.getId().toString() + "$" + TRIVIA_ANSWER_BUTTON + "$" + "D$" + triviaQuestion.getCorrectAnswer() + "$" + triviaQuestion.getPoints(), "D");

                guild.getTextChannelById(channelId)
                        .sendMessageEmbeds(embed)
                        .setActionRow(buttonA, buttonB, buttonC, buttonD)
                        .queue();
                //  .queue(sentMessage -> messageId.set(sentMessage.getId()));
            }

            messageCounter.getAndIncrement();
        }, 30, 3, TimeUnit.SECONDS);
    }

    private String findThumbnailUrl(int i, String complexity) {

        if (complexity.equalsIgnoreCase("medium")) {

            return switch (i) {
                case 1 -> "https://i.ibb.co/kQFvNMH/one-medium.png";
                case 2 -> "https://i.ibb.co/PWRF9Kt/two-medium.png";
                case 3 -> "https://i.ibb.co/JqqQnQ3/three-medium.png";
                case 4 -> "https://i.ibb.co/R39NVvw/four-medium.png";
                case 5 -> "https://i.ibb.co/kh8tQR3/five-medium.png";
                default -> "";
            };
        } else {

            return switch (i) {
                case 1 -> "https://i.ibb.co/J5VzXGk/one.png";
                case 2 -> "https://i.ibb.co/VBRScXH/two.png";
                case 3 -> "https://i.ibb.co/2hX0jfn/three.png";
                case 4 -> "https://i.ibb.co/ns1HpHx/four.png";
                case 5 -> "https://i.ibb.co/BLf5ppZ/five.png";
                case 6 -> "https://i.ibb.co/stCQt80/six.png";
                case 7 -> "https://i.ibb.co/qRsk9y8/seven.png";
                case 8 -> "https://i.ibb.co/BBcGq9M/eight.png";
                case 9 -> "https://i.ibb.co/N91wZdF/nine.png";
                case 10 -> "https://i.ibb.co/B4NLyz3/ten.png";
                default -> "";
            };
        }
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
            TriviaAnswer answer = triviaRepository.findUserAnswer(userId, questionId);
            String dateFormatted = answer.getCreatedOn().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            event.reply("Не може да отговаряте повече от веднъж! Отговора, който вече изпратихте е: \"" + answer.getUserAnswer().toUpperCase() + "\" | Изпратен на: " + dateFormatted).setEphemeral(true).queue();
            return;
        }

        String buttonLabel = event.getButton().getLabel().toLowerCase();
        String correctAnswer = buttonId.split("\\$")[3];
        int points = Integer.parseInt(buttonId.split("\\$")[4]);

        if (buttonLabel.equalsIgnoreCase(correctAnswer)) {
            event.reply("<:1760checkmarkicon:1018435285786308658> Получихме вашия отговор и той е **правилен!** :tada:").setEphemeral(true).queue();
            triviaRepository.insertAnswer(userId, buttonId, buttonLabel, true, points);
        } else {
            event.reply("<:5060crossmarkicon:1018435287828942899> Получихме вашия отговор, но той е **грешен!** :pleading_face:").setEphemeral(true).queue();
            triviaRepository.insertAnswer(userId, buttonId, buttonLabel, false, 0);
        }

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
            case EASY -> new Color(86, 196, 62);
            case MEDIUM -> new Color(70, 188, 217);
            case HARD -> new Color(227, 117, 46);
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
            case "EASY", "MEDIUM", "HARD" -> true;
            default -> false;
        };
    }

    private String findAppropriateChannel(String id, String group) {

        if (SOFTUNI_PROGRAMMING_BASICS_GUILD_ID.equals(id)) {

            return switch (group) {
                case JAVA_GROUP -> "1118283652359520387";
                case CSHARP_GROUP -> "1118283857817509959";
                case PYTHON_GROUP -> "1118284289843404970";
                case JS_GROUP -> "1118283922594340874";
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
            case MEDIUM -> "Средна";
            case HARD -> "Трудна";
        };
    }
}
