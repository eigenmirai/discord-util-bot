package io.github.mirai42.command.misc;

import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.Instant;
import java.util.Objects;

public class PollCommand {
    private static final String[] emotes = new String[]{"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"poll".equals(event.getName())) return;

        String title = Objects.requireNonNull(event.getOption("title")).getAsString();
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Util.blue)
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getEffectiveAvatarUrl())
                .setTitle(title)
                .setTimestamp(Instant.now());

        for (int i = 1; i < 10; i++) {
            OptionMapping opt = event.getOption("option-" + i);
            if (opt != null) {
                embed.getDescriptionBuilder().append(String.format("%s %s\n", emotes[i-1], opt.getAsString()));
            }
        }
        event.replyEmbeds(embed.build()).complete().retrieveOriginal().queue(msg -> {
            for (int i = 1; i < 10; i++) {
                OptionMapping opt = event.getOption("option-" + i);
                if (opt != null) {
                    msg.addReaction(Emoji.fromUnicode(emotes[i-1])).queue();
                }
            }
        });

    }
}
