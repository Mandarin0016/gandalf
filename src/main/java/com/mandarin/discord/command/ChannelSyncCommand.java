package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildCategory;
import com.mandarin.discord.enums.GuildRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class ChannelSyncCommand extends ListenerAdapter {

    public static final String CHANNEL_SYNC_COMMAND_NAME = "sync";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        boolean access = verifyCommandAccess(event,
                CHANNEL_SYNC_COMMAND_NAME,
                SOFTUNI_PROGRAMMING_BASICS_GUILD_ID,
                List.of(GuildRole.EVENT_MANAGER, GuildRole.GLOBAL_MODERATOR));

        if (!access) {
            return;
        }

        Guild guild = event.getGuild();

        Category java = guild.getCategoryById(GuildCategory.PB_JAVA.getId());
        Category cSharp = guild.getCategoryById(GuildCategory.PB_CSHARP.getId());
        Category python = guild.getCategoryById(GuildCategory.PB_PYTHON.getId());
        Category javaScript = guild.getCategoryById(GuildCategory.PB_JAVASCRIPT.getId());
        Category cPlusPlus = guild.getCategoryById(GuildCategory.PB_CPLUSPLUS.getId());
        Category improvement = guild.getCategoryById(GuildCategory.PB_IMPROVEMENT.getId());

        java.getTextChannels().forEach(c -> c.getManager().sync().queue());
        cSharp.getTextChannels().forEach(c -> c.getManager().sync().queue());
        python.getTextChannels().forEach(c -> c.getManager().sync().queue());
        javaScript.getTextChannels().forEach(c -> c.getManager().sync().queue());
        cPlusPlus.getTextChannels().forEach(c -> c.getManager().sync().queue());
        improvement.getTextChannels().forEach(c -> c.getManager().sync().queue());

        event.reply("All channels are synced.").queue();
    }
}
