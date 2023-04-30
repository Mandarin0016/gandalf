package com.mandarin.discord.util;

import com.mandarin.discord.enums.GuildRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.service.MemberService.isMissingAppropriateRole;

public class GuildAccessVerifier {

    public static boolean verifyCommandAccess(SlashCommandInteractionEvent event, String commandName, String guildId, List<GuildRole> rolesAllowed) {

        if (!event.getName().equalsIgnoreCase(commandName)) {
            return false;
        }
        if (!Objects.requireNonNull(event.getGuild()).getId().equals(guildId)) {
            event.reply("Not enable for your server!").setEphemeral(true).queue();
            return false;
        }

        if (isMissingAppropriateRole(Objects.requireNonNull(event.getMember()), rolesAllowed)) {
            event.reply("You don't have access to perform this action. Please contact a member that has <@&886273306028834816> role and ask for assistance.").setEphemeral(true).queue();
            return false;
        }

        return true;
    }
}
