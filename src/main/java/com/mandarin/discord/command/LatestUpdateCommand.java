package com.mandarin.discord.command;

import com.mandarin.discord.enums.GuildRole;
import com.mandarin.discord.repository.UpdateRepository;
import com.mandarin.discord.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.mandarin.discord.config.ApplicationConfiguration.getDefaultInternalEnvConfig;
import static com.mandarin.discord.config.GuildStartupConfiguration.SOFTUNI_PROGRAMMING_BASICS_GUILD_ID;
import static com.mandarin.discord.util.GuildAccessVerifier.verifyCommandAccess;

public class LatestUpdateCommand extends ListenerAdapter {

    public static final String LATEST_UPDATE_NOTIFY_COMMAND = "latest-update-notify";
    private final UpdateRepository updateRepository;

    public LatestUpdateCommand() {

        updateRepository = new UpdateRepository();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(LATEST_UPDATE_NOTIFY_COMMAND)) {
            return;
        }

        boolean access = verifyCommandAccess(
                event,
                LATEST_UPDATE_NOTIFY_COMMAND,
                java.util.List.of(SOFTUNI_PROGRAMMING_BASICS_GUILD_ID),
                List.of(GuildRole.EVENT_MANAGER_BASICS));

        if (!access) {
            return;
        }

        String versionInput = event.getOption("version").getAsString();
        String details = event.getOption("details").getAsString();

        String version = versionInput.equalsIgnoreCase("latest")
                ? getDefaultInternalEnvConfig().get("VERSION")
                : versionInput;

        Map<String, String> updateEntry = updateRepository.findUpdateEntry(version);

        if (updateEntry == null) {
            String message = "Missing update entry for Gandalf with the given version: **" + version + "**";
            event.reply(MessageCreateData.fromEmbeds(MessageUtil.generateFailureResponseEmbed(message, event.getMember().getEffectiveName()))).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Update Announcement", "https://github.com/Mandarin0016/gandalf/blob/master/CHANGES.md");
        builder.setColor(new Color(0x275ADE));
        builder.setThumbnail("https://i.ibb.co/wBPDJPW/MTG-Gandalf-LTR.png");
        builder.setDescription("Version: **" + updateEntry.get("version") + "**");
        builder.addField("New Features:", findNew(updateEntry.get("new")), false);
        builder.addField("Changes:", findChange(updateEntry.get("change")), false);
        builder.addField("Removals:", findRemove(updateEntry.get("remove")), false);
        builder.addField("", "*Updated on:* " + updateEntry.get("createdOn") + "\n*Developers:* " + findDevelopers(updateEntry.get("developers"), event), false);
        builder.addField(details, "", false);
        builder.setFooter("Update Entry", "https://i.ibb.co/wBPDJPW/MTG-Gandalf-LTR.png");
        builder.setTimestamp(Instant.now());

        MessageEmbed embed = builder.build();
        event.reply(MessageCreateData.fromEmbeds(embed)).queue();
    }

    private String findDevelopers(String developersRow, SlashCommandInteractionEvent event) {
        return Arrays.stream(developersRow.split("\\$")).map(id -> event.getGuild().getMemberById(id).getAsMention()).collect(Collectors.joining(", "));
    }

    private String findRemove(String removeRow) {
        AtomicInteger rowCounter = new AtomicInteger(1);
        List<String> removeItems = Arrays.stream(removeRow.split("\\$")).map(e -> String.format("%d. %s", rowCounter.getAndIncrement(), e)).toList();
        return removeItems.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private String findChange(String changeRow) {
        AtomicInteger rowCounter = new AtomicInteger(1);
        List<String> removeItems = Arrays.stream(changeRow.split("\\$")).map(e -> String.format("%d. %s", rowCounter.getAndIncrement(), e)).toList();
        return removeItems.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private String findNew(String newRow) {
        AtomicInteger rowCounter = new AtomicInteger(1);
        List<String> removeItems = Arrays.stream(newRow.split("\\$")).map(e -> String.format("%d. %s", rowCounter.getAndIncrement(), e)).toList();
        return removeItems.stream().collect(Collectors.joining(System.lineSeparator()));
    }

}
