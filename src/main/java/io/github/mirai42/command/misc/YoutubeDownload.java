package io.github.mirai42.command.misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class YoutubeDownload {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"yt-download".equals(event.getName())) return;

        String url = Objects.requireNonNull(event.getOption("url")).getAsString();
        boolean extractAudio = Objects.requireNonNull(event.getOption("extract-audio")).getAsBoolean();
        String file = "";
        String command = buildCommand(url, file, extractAudio);
        try {
            String[] cmd = {"/bin/sh", "-c", command};
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageEmbed embed = new EmbedBuilder()
                .build();
        FileUpload attachment = FileUpload.fromData(new File(file));

    }

    private String buildCommand(String url, String file, boolean extractAudio) {
        return String.format("yt-dlp -P $HOME/.cache -o \"%%(title)s.%%(ext)s\" %s%s --no-exec", extractAudio ? "-x " : "", url);
    }
}
