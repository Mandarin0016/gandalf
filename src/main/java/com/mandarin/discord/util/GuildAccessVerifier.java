package com.mandarin.discord.util;

import com.mandarin.discord.enums.GuildRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.service.MemberService.isMissingAppropriateRole;

public class GuildAccessVerifier {

    public static boolean verifyCommandAccess(SlashCommandInteractionEvent event, String commandName, List<String> guildIds, List<GuildRole> rolesAllowed) {

        if (!event.getName().equalsIgnoreCase(commandName)) {
            return false;
        }

        if (!guildIds.contains(Objects.requireNonNull(event.getGuild()).getId())) {
            event.reply("Not enable for your server!").setEphemeral(true).queue();
            return false;
        }

            if (!rolesAllowed.isEmpty() && isMissingAppropriateRole(Objects.requireNonNull(event.getMember()), rolesAllowed)) {
                event.reply("You don't have access to perform this action. Please contact a member that has **admin/moderator** role and ask for assistance.").setEphemeral(true).queue();
                return false;
            }

        return true;
    }

    public static boolean verifyButtonAccessToEventListener(ButtonInteractionEvent event, String buttonIdentifier) {

        return Objects.requireNonNull(event.getButton().getId()).split("\\$")[1].equalsIgnoreCase(buttonIdentifier);
    }
}
