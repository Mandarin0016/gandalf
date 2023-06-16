package com.mandarin.discord.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

@UtilityClass
public class MessageUtil {

    public static MessageEmbed generateFailureResponseEmbed(String information, String author) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Response status: FAILURE <:5060crossmarkicon:1018435287828942899>");
        builder.setColor(new Color(0xE11D1D));
        builder.setDescription(information);
        builder.setFooter("Action caused by " + author);
        builder.setTimestamp(Instant.now());

        return builder.build();
    }

    public static MessageEmbed generateSuccessResponseEmbed(String information, String author) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Response status: SUCCESS <:1760checkmarkicon:1018435285786308658>");
        builder.setColor(new Color(0x14B71C));
        builder.setDescription(information);
        builder.setFooter("Action caused by " + author);
        builder.setTimestamp(Instant.now());

        return builder.build();
    }
}
