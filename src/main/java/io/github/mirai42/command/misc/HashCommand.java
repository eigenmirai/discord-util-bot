package io.github.mirai42.command.misc;

import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Objects;
import java.util.Set;

public class HashCommand {
    public static Set<String> ALGORITHMS = Security.getAlgorithms("MessageDigest");

    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"hash".equals(event.getName())) return;

        String s = Objects.requireNonNull(event.getOption("string")).getAsString();
        String algorithm;
        OptionMapping algorithmOption = event.getOption("algorithm");
        if (algorithmOption == null) {
            algorithm = "MD5";
        } else {
            algorithm = algorithmOption.getAsString();
            if (!ALGORITHMS.contains(algorithm)) {
                algorithm = "MD5";
            }
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            Embeds.errorEmbed(e.getMessage());
            return;
        }
        digest.update(s.getBytes(StandardCharsets.UTF_8));
        String hash = Util.byteArray2Hex(digest.digest());

        event.replyEmbeds(Embeds.ioEmbed(String.format("Hash computed (%s)", algorithm), s, hash, null)).queue();
    }
}
