package io.github.mirai42.command.misc;

import io.github.mirai42.util.Util;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Slf4j
public class YoutubeDownload {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"yt-download".equals(event.getName())) return;

        event.deferReply().queue();

        String url = Objects.requireNonNull(event.getOption("url")).getAsString();
        OptionMapping extractAudioOpt = event.getOption("extract-audio");
        boolean extractAudio;
        if (extractAudioOpt == null) {
            extractAudio = false;
        } else {
            extractAudio = extractAudioOpt.getAsBoolean();
        }

        String command = buildCommand(url, extractAudio);
        String title = getVideoTitle(url);
        String ext = extractAudio ? ".opus" : ".webm";
        File file = new File(title + ext);
        log.info("Running command: " + command);

        log.info("Downloading video " + url);
        try {
            String[] cmd = {"/bin/sh", "-c", command};
            ProcessBuilder pb = new ProcessBuilder(cmd);
            //pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        // longer videos not implemented yet (im lazy as fuck)
        if (file.length() > 25_000_000) {
            embedBuilder.setColor(Util.red)
                    .setTitle(":x: An error occurred")
                    .setDescription("Video file is too large.")
                    .setTimestamp(Instant.now());
        } else {
            FileUpload attachment = FileUpload.fromData(file);
            messageBuilder.addFiles(attachment);
            embedBuilder.setColor(Util.green)
                    .setTitle(":white_check_mark: Video downloaded")
                    .setDescription("`" + title + "`")
                    .setTimestamp(Instant.now());
        }
        messageBuilder.addEmbeds(embedBuilder.build());

        MessageCreateData message = messageBuilder.build();
        event.getHook().sendMessage(message).queue();

        if (!file.delete()) {
            log.info("Could not delete video file.");
        }
    }

    private String buildCommand(String url, boolean extractAudio) {
        String title = getVideoTitle(url);
        String x = extractAudio ? "-x " : "";
        String ext = extractAudio ? "" : ".webm";
        return String.format("yt-dlp %s -o \"%s%s\" %s --no-exec", x, title, ext, url);
    }

    private String getVideoTitle(String url) {
        String title;
        try {
            String[] cmd = {"/bin/sh", "-c", String.format("curl --silent %s | grep '<title>' | sed 's-.*<title>--g; s-<\\/title>.*--g'", url)};
            Process p = Runtime.getRuntime().exec(cmd);
            title = new String(p.getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        title = title.substring(0, title.length() - 11);
        return title.replace("\n", "").replace(" ", "_");
    }

}
