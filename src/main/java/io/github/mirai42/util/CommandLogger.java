package io.github.mirai42.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

@Slf4j
public class CommandLogger {
    public static int commandCounter = 0;

    @SubscribeEvent
    public void logCommands(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        log.info(String.format("[%s] Command %s executed by %s (%s) in server %s (%s)",
                Util.time(), event.getName(), user.getAsTag(), user.getId(), event.getGuild().getName(), event.getGuild().getId()));
        CommandLogger.commandCounter++;
    }
}
