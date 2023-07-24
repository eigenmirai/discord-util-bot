package io.github.mirai42.command.admin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PurgeCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"purge".equals(event.getName())) return;

        int amount = Objects.requireNonNull(event.getOption("amount")).getAsInt();
        OptionMapping userOption = event.getOption("user");
        event.deferReply().queue();

        StringBuffer log = new StringBuffer();
        Guild guild = event.getGuild();

        var a = event.getChannel().getHistory().getRetrievedHistory();
        if (userOption != null) {
            User user = userOption.getAsUser();
            a = a.stream().filter(e -> e.getAuthor().equals(user)).toList();
        }

        int count = 0;
        for (var e : a) {
            if (count == amount) break;
            log.append(String.format("[%s] %s: ",
                    e.getTimeCreated().toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    e.getMember().getUser().getAsTag()));
            log.append(e.getContentRaw());
            log.append("\n");
            e.delete().queue();
            count++;
        }
    }
}
