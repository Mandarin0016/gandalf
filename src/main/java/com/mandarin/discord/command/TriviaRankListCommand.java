package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.mandarin.discord.config.GuildStartupConfiguration.*;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class TriviaRankListCommand extends ListenerAdapter {

    public static final String RANK_LIST_COMMAND_NAME = "rank-list";
    public final TriviaRepository triviaRepository;

    public TriviaRankListCommand() {
        this.triviaRepository = new TriviaRepository();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(RANK_LIST_COMMAND_NAME)) {
            return;
        }

        boolean access = verifyCommandAccess(
                event,
                RANK_LIST_COMMAND_NAME,
                java.util.List.of(
                        SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                        TRIVIA_TEST_GUILD_ID),
                List.of(
                        GuildRole.GLOBAL_MODERATOR_BASICS,
                        GuildRole.EVENT_MANAGER_BASICS,
                        GuildRole.TESTER_TRIVIA_SERVER_ROLE
                ));

        if (!access) {
            return;
        }

        String flag = Objects.requireNonNull(event.getOption("flag")).getAsString();

        switch (flag) {
            case "global" -> event.reply(MessageCreateData.fromEmbeds(buildGlobalRankListEmbed(event))).queue();
            case "java" -> event.reply(MessageCreateData.fromEmbeds(buildGroupRankListEmbed(event, "java"))).queue();
            case "c#" -> event.reply(MessageCreateData.fromEmbeds(buildGroupRankListEmbed(event, "c#"))).queue();
            case "python" -> event.reply(MessageCreateData.fromEmbeds(buildGroupRankListEmbed(event, "python"))).queue();
            case "js" -> event.reply(MessageCreateData.fromEmbeds(buildGroupRankListEmbed(event, "js"))).queue();
            default -> event.reply("Incorrect flag!").queue();
        }
    }

    public MessageEmbed buildGroupRankListEmbed(Event event, String group) {

        Set<String> groupsRankLists = triviaRepository.findGroupRankList(group);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(toGroupRankListTitle(group.toLowerCase()) + " Trivia Ranklist: ");
        builder.setColor(new Color(0xFF7707));
        builder.setThumbnail("https://img.freepik.com/free-vector/top-10-best-podium-award_52683-52572.jpg?w=826&t=st=1686838736~exp=1686839336~hmac=f8278b6dd82a172407746cfb851d379db1cc30b5acc025a3b9cceb7565d14417");
        builder.setDescription("Топ **10**");
        builder.setFooter("Trivia Edition by Gandalf", "https://cdnb.artstation.com/p/assets/images/images/053/635/185/large/ainhoa-del-valle-gandalf-800.jpg?1662657800");
        builder.setTimestamp(OffsetDateTime.now());

        Iterator<String> iterator = groupsRankLists.iterator();
        for (int i = 1; i <= 10; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            String person = iterator.next();
            String getNickname = event.getJDA().getGuildById(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID).getMemberById(person.split("\\$")[0]).getEffectiveName();
            String icon = findGroupIcon(i);
            String points = person.split("\\$")[1];
            String fieldValue = String.format("**%d.** **%s**  %s  *(%s точки)*", i, getNickname, icon, points);
            builder.addField("", fieldValue, false);
        }

        fillGaps(groupsRankLists.size(), builder);

        return builder.build();
    }

    private String toGroupRankListTitle(String group) {

        return switch (group) {
            case "js" -> "JavaScript";
            case "c#" -> "C#";
            case "java" -> "Java";
            case "python" -> "Python";
            default -> "Unknown";
        };
    }

    public MessageEmbed buildGlobalRankListEmbed(Event event) {

        Set<String> globalRankList = triviaRepository.findGlobalRankList();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Global Trivia Ranklist: ");
        builder.setColor(new Color(0xFF7707));
        builder.setThumbnail("https://img.freepik.com/free-vector/gold-cup-illustration_1284-17139.jpg?w=826&t=st=1686837967~exp=1686838567~hmac=e8251fbc7b988aacbfad278c92d69d96ec13ce62d3933eef3ac2f90b0ea5963b");
        builder.setDescription("Топ **10**");
        builder.setFooter("Trivia Edition by Gandalf", "https://cdnb.artstation.com/p/assets/images/images/053/635/185/large/ainhoa-del-valle-gandalf-800.jpg?1662657800");
        builder.setTimestamp(OffsetDateTime.now());

        Iterator<String> iterator = globalRankList.iterator();
        for (int i = 1; i <= 10; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            String person = iterator.next();
            String getNickname = event.getJDA().getGuildById(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID).getMemberById(person.split("\\$")[0]).getEffectiveName();
            String icon = findGlobalIcon(i);
            String points = person.split("\\$")[1];
            String fieldValue = String.format("**%d.** **%s**  %s  *(%s точки)*", i, getNickname, icon, points);
            builder.addField("", fieldValue, false);
        }

        fillGaps(globalRankList.size(), builder);

        return builder.build();
    }

    private void fillGaps(int size, EmbedBuilder builder) {
        for (int i = size + 1; i <= 10; i++) {
            String fieldValue = String.format("**%d.** ------  --  *(-- точки)*", i);
            builder.addField("", fieldValue, false);
        }
    }

    private String findGlobalIcon(int i) {

        return switch (i) {
            case 1 -> "<:goldmedal:1117506449665441924>";
            case 2 -> "<:silvermedal:1117506446934933555>";
            case 3 -> "<:bronzemedal:1117506443529158677>";
            case 4, 5 -> "<:4thglobal:1118209371575439410>";
            case 6, 7, 8, 9, 10 -> "<:5thglobal:1118209367536316456>";
            default -> "";
        };
    }

    private String findGroupIcon(int i) {
        return switch (i) {
            case 1 -> "<:groupgoldmedal:1118207518678720652>";
            case 2 -> "<:groupsilvermedal:1118207469232078869>";
            case 3 -> "<:groupbronzemedal:1118207473011150889>";
            case 4 -> "<:4th:1118209377715900568>";
            case 5 -> "<:5th:1118209381604003840>";
            case 6, 7, 8, 9, 10 -> "<:rest:1118209374477877300>";
            default -> "";
        };
    }
}
