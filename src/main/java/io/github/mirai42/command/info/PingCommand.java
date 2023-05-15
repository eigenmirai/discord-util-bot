package io.github.mirai42.command.info;

import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PingCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"ping".equals(event.getName())) return;

        Message sentMessage = event.getChannel().sendMessage("Calculating...").complete();
        long diff = event.getTimeCreated().until(sentMessage.getTimeCreated(), ChronoUnit.MILLIS);

        String botLatency = String.format("%dms", diff);
        String apiLatency = String.format("%dms", event.getJDA().getGatewayPing());
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Util.blue)
                .setTitle(":ping_pong: Pong")
                .addField("Bot latency", MarkdownUtil.codeblock(botLatency), true)
                .addField("API Latency", MarkdownUtil.codeblock(apiLatency), true)
                .setTimestamp(Instant.now())
                .build();
        event.replyEmbeds(embed).queue();
        sentMessage.delete().queue();
    }
}
