package com.mandarin.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;

public class TriviaTriggerCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getGuild().getId().equals("1092768978285367316") && !event.getName().equalsIgnoreCase("trivia-start")) {
            return;
        }

        Guild guild = event.getGuild();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Example Embed");
        builder.setDescription("This is an example of an embed with a submit form.");
        builder.addField("Field 1", "Value 1", true);
        builder.addField("Field 2", "Value 2", true);
        builder.setFooter("Footer Text", null);
        builder.setTimestamp(Instant.now());

        MessageEmbed embed = builder.build();

        guild.getTextChannelById("1093637377307713636")
                .sendMessageEmbeds(embed)
                .queue();

        event.reply("Trivia has started!").queue();
    }
}
