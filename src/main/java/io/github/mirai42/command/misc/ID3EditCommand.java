package io.github.mirai42.command.misc;

import io.github.mirai42.id3lib.v1.ID3v1Tags;
import io.github.mirai42.util.Embeds;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.requests.Requester;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

// wip
public class ID3EditCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"edit-id3".equals(event.getName())) return;

        OptionMapping fileOption = event.getOption("file");
        Message.Attachment attachment = fileOption.getAsAttachment();
        if (!"mp3".equals(attachment.getFileExtension())) {
            event.replyEmbeds(Embeds.errorEmbed("File has to be an mp3 file.")).queue();
            return;
        }

        Path savePath = Path.of(System.getProperty("io.tmpdir"), attachment.getFileName());
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("User-Agent", Requester.USER_AGENT)
                    .uri(new URI(attachment.getUrl()))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofFile(savePath));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            event.replyEmbeds(Embeds.errorEmbed(e.toString())).queue();
            return;
        }
        if (!Files.exists(savePath)) {
            event.replyEmbeds(Embeds.errorEmbed("An error occurred: failed to save file")).queue();

        }
        ID3v1Tags tags = ID3v1Tags.read(savePath.toFile());

    }
}
