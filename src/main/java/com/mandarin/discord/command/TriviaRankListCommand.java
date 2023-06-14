package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
            case "global" -> buildGlobalRankListAndReply(event);
            default -> event.reply("Incorrect flag!").queue();
        }
    }

    private void buildGlobalRankListAndReply(SlashCommandInteractionEvent event) {

        Set<String> globalRankList = triviaRepository.getGlobalRankList();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Global Trivia Ranklist: ");
        builder.setColor(new Color(0xFF7707));
        builder.setThumbnail("https://img.freepik.com/free-vector/ranked-no-1-business-label-design_1017-12401.jpg?w=826&t=st=1686772559~exp=1686773159~hmac=667bd7ae8f8a2ec3e74b3dec4aeb63766276f290fd002b905ecb9d84fb8e8645");
        builder.setDescription("Топ **10**");
        builder.setFooter("Trivia Edition by Gandalf", "https://cdnb.artstation.com/p/assets/images/images/053/635/185/large/ainhoa-del-valle-gandalf-800.jpg?1662657800");
        builder.setTimestamp(OffsetDateTime.now());

        Iterator<String> iterator = globalRankList.iterator();
        for (int i = 1; i <= 10; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            String person = iterator.next();
            String getNickname = event.getGuild().getMemberById(person.split("\\$")[0]).getEffectiveName();
            String icon = findGlobalIcon(i);
            String points = person.split("\\$")[1];
            String fieldValue = String.format("**%d.** **%s**  %s  *(%s точки)*", i, getNickname, icon, points);
            builder.addField("", fieldValue, false);
        }

        fillGaps(globalRankList.size(), builder);

        MessageEmbed embed = builder.build();

        event.reply(MessageCreateData.fromEmbeds(embed)).queue();
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
}
