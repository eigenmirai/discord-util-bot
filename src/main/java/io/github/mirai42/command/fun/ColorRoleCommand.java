package io.github.mirai42.command.fun;

import io.github.mirai42.util.Embeds;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

public class ColorRoleCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"color-role".equals(event.getName())) return;

        String hex = Objects.requireNonNull(event.getOption("hex")).getAsString();
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        Color color;
        try {
            color = Color.decode(hex);
        } catch (Exception e) {
            event.replyEmbeds(Embeds.errorEmbed("Invalid color code")).queue();
            e.printStackTrace();
            return;
        }
        Member member = event.getMember();
    }
}
