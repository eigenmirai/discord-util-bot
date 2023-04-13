package io.github.mirai42.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class Embeds {
    public static MessageEmbed errorEmbed(String message) {
        return new EmbedBuilder()
                .setColor(Util.red)
                .setTitle(":x: An error occurred")
                .setDescription(message)
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed ioEmbed(String title, String inputField, String outputField, @Nullable String moreInfo) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Util.green)
                .setTitle(":white_check_mark: " + title)
                .addField(":inbox_tray: Input", MarkdownUtil.codeblock(inputField), false)
                .addField(":outbox_tray: Output", MarkdownUtil.codeblock(outputField), false)
                .setTimestamp(Instant.now());
        if (moreInfo != null) {
            builder.setFooter(moreInfo);
        }
        return builder.build();
    }
}
