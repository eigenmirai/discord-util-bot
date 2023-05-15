package io.github.mirai42.command.misc;

import io.github.mirai42.Bot;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Objects;

public class WolframAlphaCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"wa".equals(event.getName())) return;

        String appId = Bot.getInstance().getConfig().getProperty("bot.wolfram.id");
        if (appId == null) {
            event.replyEmbeds(Embeds.errorEmbed("AppID not found! Please contact the developers.")).queue();
            return;
        }
        String base = "http://api.wolframalpha.com/v1/simple?appid=" + appId;

        String input = Objects.requireNonNull(event.getOption("input")).getAsString()
                .replace(" ", "+");
        String url = base + "&i=" + input + "&units=metric&background=35393e&foreground=white";
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Util.green)
                .setTitle(":white_check_mark: ")
                .addField(":inbox_tray: Input", MarkdownUtil.codeblock(input), false)
                .addField(":outbox_tray: Output", "", false)
                .setImage(url)
                .build();
        event.replyEmbeds(embed).queue();
    }
}
