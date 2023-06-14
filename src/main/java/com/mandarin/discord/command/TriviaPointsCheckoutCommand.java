package com.mandarin.discord.command;

import com.mandarin.discord.entity.TriviaUserInfo;
import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mandarin.discord.config.GuildStartupConfiguration.*;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class TriviaPointsCheckoutCommand extends ListenerAdapter {
    public static final String TRIVIA_POINTS_CHECKOUT_COMMAND_NAME = "points";
    public final TriviaRepository triviaRepository;

    public TriviaPointsCheckoutCommand() {
        this.triviaRepository = new TriviaRepository();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(TRIVIA_POINTS_CHECKOUT_COMMAND_NAME)) {
            return;
        }

        boolean access = verifyCommandAccess(
                event,
                TRIVIA_POINTS_CHECKOUT_COMMAND_NAME,
                java.util.List.of(
                        SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                        TRIVIA_TEST_GUILD_ID),
                List.of());

        if (!access) {
            return;
        }

        String userId = event.getMember().getId();

        TriviaUserInfo userInfo = triviaRepository.getUserPoints(userId);
        Map<String, String> ranks = triviaRepository.getUserRank(userId, event.getMember().getRoles().stream().map(Role::getName).map(String::toUpperCase).collect(Collectors.toList()));

        Integer collectedPoints = userInfo.getGroupPoints().values().stream().mapToInt(Integer::intValue).sum();
        Integer answeredQuestionsCount = userInfo.getAnsweredQuestions().values().stream().mapToInt(Integer::intValue).sum();
        Integer correctAnswersCount = userInfo.getCorrectAnswers().values().stream().mapToInt(Integer::intValue).sum();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Здравей :wave:, това са твойте резултати:");
        builder.setDescription(String.format(
                "*Събрани точки:* **%d**%n*Отговорени въпроси:* **%d**%n*Верни отговори:* **%d**%n",
                collectedPoints, answeredQuestionsCount, correctAnswersCount));
        String javaFieldValue = String.format(
                "**%d** *(%d/%d)*", userInfo.getGroupPoints().get("Java"), userInfo.getCorrectAnswers().get("Java"), userInfo.getAnsweredQuestions().get("Java")
        );
        String cSharpFieldValue = String.format(
                "**%d** *(%d/%d)*", userInfo.getGroupPoints().get("C#"), userInfo.getCorrectAnswers().get("C#"), userInfo.getAnsweredQuestions().get("C#")
        );
        String jsFieldValue = String.format(
                "**%d** *(%d/%d)*", userInfo.getGroupPoints().get("JS"), userInfo.getCorrectAnswers().get("JS"), userInfo.getAnsweredQuestions().get("JS")
        );
        String pythonFieldValue = String.format(
                "**%d** *(%d/%d)*", userInfo.getGroupPoints().get("Python"), userInfo.getCorrectAnswers().get("Python"), userInfo.getAnsweredQuestions().get("Python")
        );
        builder.addField("__JS__", jsFieldValue, true);
        builder.addField("__C#__", cSharpFieldValue, true);
        builder.addField("__Java__", javaFieldValue, true);
        builder.addField("__Python__", pythonFieldValue, true);
        builder.addField("__C++__", "~~**0** *(0/0)*~~", true);
        builder.addField("__Go__", "~~**0** *(0/0)*~~", true);
        builder.setColor(event.getMember().getColor());
        builder.setThumbnail(event.getUser().getEffectiveAvatarUrl());
        builder.setFooter("Trivia Edition by Gandalf", "https://cdnb.artstation.com/p/assets/images/images/053/635/185/large/ainhoa-del-valle-gandalf-800.jpg?1662657800");
        builder.setTimestamp(OffsetDateTime.now());

        for (Map.Entry<String, String> rank : ranks.entrySet()) {
            if (rank.getKey().equals("**Global rank**")) {
                builder.addField("", "__" + rank.getKey() + "__: **" + rank.getValue() + "** " + possibleGlobalIcon(rank.getValue()), false);
            } else {
                builder.addField("", "__" + rank.getKey() + "__: **" + rank.getValue() + "** " + possibleGroupRankIcon(rank.getValue()), false);
            }
        }

        MessageEmbed embed = builder.build();

        event.reply(MessageCreateData.fromEmbeds(embed)).queue();
    }

    private String possibleGlobalIcon(String value) {
        return switch (value) {
            case "1" -> "<:goldmedal:1117506449665441924>";
            case "2" -> "<:silvermedal:1117506446934933555>";
            case "3" -> "<:bronzemedal:1117506443529158677>";
            case "4", "5" -> "<:4thglobal:1118209371575439410>";
            case "6", "7", "8", "9", "10" -> "<:5thglobal:1118209367536316456>";
            default -> "";
        };
    }

    private String possibleGroupRankIcon(String value) {
        return switch (value) {
            case "1" -> "<:groupgoldmedal:1118207518678720652>";
            case "2" -> "<:groupsilvermedal:1118207469232078869>";
            case "3" -> "<:groupbronzemedal:1118207473011150889>";
            case "4" -> "<:4th:1118209377715900568>";
            case "5" -> "<:5th:1118209381604003840>";
            case "6", "7", "8", "9", "10" -> "<:rest:1118209374477877300>";
            default -> "";
        };
    }
}
