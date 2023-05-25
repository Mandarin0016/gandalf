package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildCategory;
import com.mandarin.discord.enums.GuildRole;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class ChannelUnlockCommand extends ListenerAdapter {

    public static final String CHANNEL_UNLOCK_COMMAND_NAME = "unlock";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(event,
                CHANNEL_UNLOCK_COMMAND_NAME,
                List.of(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID),
                List.of(GuildRole.EVENT_MANAGER_BASICS, GuildRole.GLOBAL_MODERATOR_BASICS));

        if (!access) {
            return;
        }

        Guild guild = event.getGuild();

        Role javaRole = guild.getRoleById("926232154747305995");
        Role cSharpRole = guild.getRoleById("926233319715266622");
        Role pythonRole = guild.getRoleById("926233605267681322");
        Role javaScriptRole = guild.getRoleById("926232546663088179");
        Role cPlusPlusRole = guild.getRoleById("927196775494848562");

        Category java = guild.getCategoryById(GuildCategory.PB_JAVA.getId());
        Category cSharp = guild.getCategoryById(GuildCategory.PB_CSHARP.getId());
        Category python = guild.getCategoryById(GuildCategory.PB_PYTHON.getId());
        Category javaScript = guild.getCategoryById(GuildCategory.PB_JAVASCRIPT.getId());
        Category cPlusPlus = guild.getCategoryById(GuildCategory.PB_CPLUSPLUS.getId());
        Category improvement = guild.getCategoryById(GuildCategory.PB_IMPROVEMENT.getId());

        TextChannel discordSupportChannel = guild.getTextChannelById("926404226807136277");

        for (VoiceChannel voiceChannel : guild.getCategoryById("886268434004983817").getVoiceChannels()) {

            voiceChannel.getManager().setUserLimit(50).queue();
        }

        EnumSet<Permission> allowSet = EnumSet.of(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL);

        java.getManager().putPermissionOverride(javaRole, allowSet, null).queue();
        cSharp.getManager().putPermissionOverride(cSharpRole, allowSet, null).queue();
        python.getManager().putPermissionOverride(pythonRole, allowSet, null).queue();
        javaScript.getManager().putPermissionOverride(javaScriptRole, allowSet, null).queue();
        cPlusPlus.getManager().putPermissionOverride(cPlusPlusRole, allowSet, null).queue();

        discordSupportChannel.getManager()
                .putPermissionOverride(javaRole, allowSet, null)
                .putPermissionOverride(cSharpRole, allowSet, null)
                .putPermissionOverride(pythonRole, allowSet, null)
                .putPermissionOverride(javaScriptRole, allowSet, null)
                .putPermissionOverride(cPlusPlusRole, allowSet, null).queue();

        improvement.getManager()
                .putPermissionOverride(javaRole, allowSet, null)
                .putPermissionOverride(cSharpRole, allowSet, null)
                .putPermissionOverride(pythonRole, allowSet, null)
                .putPermissionOverride(javaScriptRole, allowSet, null)
                .putPermissionOverride(cPlusPlusRole, allowSet, null).queue();

        String completeMessage = String.format("""
                %s unlocked all channels!
                """, Objects.requireNonNull(event.getMember()).getAsMention());
        event.reply(completeMessage).queue();
    }
}
