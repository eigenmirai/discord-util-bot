package io.github.mirai42.command.fun;

import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class CoinflipCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"coinflip".equals(event.getName())) return;

        String result = Math.random() <= 0.5 ? "Heads" : "Tails";
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Util.blue)
                .setTitle(result)
                .build();
        event.replyEmbeds(embed).queue();
    }
}
