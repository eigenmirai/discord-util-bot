package io.github.mirai42.command.dev;

import io.github.mirai42.Bot;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.Presence;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ActivityCommand {
    public static final List<String> activities = List.of("PLAYING", "WATCHING", "LISTENING", "STREAMING", "COMPETING");

    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"set-activity".equals(event.getName())) return;

        String activityType = Objects.requireNonNull(event.getOption("type")).getAsString();
        String activityText = Objects.requireNonNull(event.getOption("text")).getAsString();
        OptionMapping streamingUrlOption = event.getOption("url");

        if (streamingUrlOption == null && activityType.equalsIgnoreCase("streaming")) {
            event.replyEmbeds(Embeds.errorEmbed("URL can't be null for streaming activity")).queue();
            return;
        }

        JDA jda = Bot.getInstance().getJda();
        Presence presence = jda.getPresence();

        String oldActivity = String.format("%s **%s**", Util.firstLetterCaps(presence.getActivity().getType().name()),
                presence.getActivity().getName());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(":station: Activity changed")
                .setColor(Util.blue)
                .addField("→ Old activity", oldActivity, true)
                .setTimestamp(Instant.now());

        if (activityType.equalsIgnoreCase("streaming")) {
            presence.setActivity(Activity.of(
                    Activity.ActivityType.STREAMING, activityText, streamingUrlOption.getAsString()));
        } else {
            presence.setActivity(Activity.of(
                    Activity.ActivityType.valueOf(activityType), activityText));
        }
        String newActivity = String.format("%s **%s**", Util.firstLetterCaps(presence.getActivity().getType().name()),
                presence.getActivity().getName());
        log.info(String.format("Activity changed to \"%s\" by user %s", newActivity, event.getUser().getAsTag()));
        embed.addField("→ New activity", newActivity, true);
        event.replyEmbeds(embed.build()).queue();
    }
}
