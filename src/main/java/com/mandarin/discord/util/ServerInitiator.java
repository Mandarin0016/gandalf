package com.mandarin.discord.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.Objects;

import static com.mandarin.discord.config.ApplicationConfiguration.isProdLoaded;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;

@UtilityClass
public class ServerInitiator {

    public static final String BASIC_SERVER_INITIATOR = "BASICS";
    public static final String FUNDAMENTALS_SERVER_INITIATOR = "FUNDAMENTALS";
    private static final String BASICS_ANNOUNCEMENT_CHANNEL_ID_PROD = "886603663961907261";
    private static final String BASICS_ANNOUNCEMENT_CHANNEL_ID_WIP = "1111272906111066234";
    private static final String FUNDAMENTALS_ANNOUNCEMENT_CHANNEL_ID_PROD = "954298971281580037";
    private static final String FUNDAMENTALS_ANNOUNCEMENT_CHANNEL_ID_WIP = "1111273018346442752";

    public static String findServerInitiator(CommandInteraction event) {

        if (Objects.requireNonNull(event.getGuild()).getId().equals(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID)) {
            return BASIC_SERVER_INITIATOR;
        } else {
            return FUNDAMENTALS_SERVER_INITIATOR;
        }
    }

    public static String findAnnouncementChannelId(String initiator) {

        if (isProdLoaded()) {

            if (initiator.equals(BASIC_SERVER_INITIATOR)) {
                return BASICS_ANNOUNCEMENT_CHANNEL_ID_PROD;
            } else {
                return FUNDAMENTALS_ANNOUNCEMENT_CHANNEL_ID_PROD;
            }
        } else {

            if (initiator.equals(BASIC_SERVER_INITIATOR)) {
                return BASICS_ANNOUNCEMENT_CHANNEL_ID_WIP;
            } else {
                return FUNDAMENTALS_ANNOUNCEMENT_CHANNEL_ID_WIP;
            }
        }
    }
}
