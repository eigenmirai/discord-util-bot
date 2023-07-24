package io.github.mirai42.command.dev;

import io.github.mirai42.Bot;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.time.Instant;

public class ShutdownCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"shutdown".equals(event.getName())) return;


        if (!Bot.getInstance().getDevelopers().contains(event.getUser())) {
            event.replyEmbeds(Embeds.errorEmbed("Permission denied: only developers are allowed to use this command")).queue();
            return;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Util.red)
                .setTitle("Shutdown")
                .setDescription("Are you sure you want to shutdown")
                .setTimestamp(Instant.now())
                .build();
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .addEmbeds(embed)
                .addComponents(ActionRow.of(
                        Button.danger("mogus", "Shutdown"),
                        Button.secondary("a", "Cancel")));
        event.reply(builder.build()).queue();

    }

    @SubscribeEvent
    public void onButtonClick(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "mogus" -> {
                event.getMessage().editMessageEmbeds(new EmbedBuilder().setColor(Util.green).setDescription("Shutting down...").build()).queue();
                System.exit(0);
            }
            case "a" ->
                    event.getMessage().editMessageEmbeds(new EmbedBuilder().setColor(Util.blue).setDescription("Shutdown cancelled").build()).queue();
            default -> {
            }
        }
    }
}
