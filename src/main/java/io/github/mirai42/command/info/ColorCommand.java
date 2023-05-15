package io.github.mirai42.command.info;

import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class ColorCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"color".equals(event.getName())) return;

        String hex = Objects.requireNonNull(event.getOption("hex")).getAsString();
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        Color color;
        try {
            color = Color.decode(hex);
        } catch (Exception e) {
            event.replyEmbeds(Embeds.errorEmbed(e.toString())).queue();
            return;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(color)
                .setTitle(":rainbow::rainbow::rainbow:")
                .addField("Hexadecimal", MarkdownUtil.codeblock(String.valueOf(hex)), true)
                .addField("Decimal", MarkdownUtil.codeblock(String.valueOf(color.getRGB())), true)
                .addField("RGB", MarkdownUtil.codeblock(Util.rgbString(color)), false)
                .addField("HSV", MarkdownUtil.codeblock(Util.hsvString(color)), true)
                .setTimestamp(Instant.now())
                .build();
        event.replyEmbeds(embed).queue();
    }
}
