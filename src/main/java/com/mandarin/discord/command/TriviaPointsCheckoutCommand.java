package com.mandarin.discord.command;

import com.mandarin.discord.repository.TriviaRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class TriviaPointsCheckoutCommand extends ListenerAdapter {
    public static final String TRIVIA_POINTS_CHECKOUT_COMMAND_NAME = "my-points";
    public  final TriviaRepository triviaRepository;
    public TriviaPointsCheckoutCommand() {
        this.triviaRepository = new TriviaRepository();
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase(TRIVIA_POINTS_CHECKOUT_COMMAND_NAME)) {
            return;
        }
        if (!event.getGuild().getId().equals("1092768978285367316")) {
            event.reply("You don't have access to perform this action. Please contact a member that has <@&886273306028834816> role and ask for assistance.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();

        String userId = event.getMember().getId();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Вашия брой точки е: " + triviaRepository.getUserPoints(userId));
        builder.setColor(Color.BLUE);
        builder.setThumbnail("https://www.cumberlandforest.com/backwoods/wp-content/uploads/2021/03/trivia-night.jpg");

        MessageEmbed embed = builder.build();

        guild.getTextChannelById("1093637377307713636")
                .sendMessageEmbeds(embed)
                .queue();

    }
}
