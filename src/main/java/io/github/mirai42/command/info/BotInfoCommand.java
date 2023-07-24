package io.github.mirai42.command.info;

import io.github.mirai42.Bot;
import io.github.mirai42.util.CommandLogger;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Info command showing information on the bot and runtime
 * Information shown: os, version, uptime, gateway ping
 */
@Slf4j
public class BotInfoCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"bot-info".equals(event.getName())) return;

        String os = System.getProperty("os.name");
        String kernel;
        try {
            if (os.toLowerCase().contains("win")) {
                kernel = System.getProperty("os.version");
            } else {
                String[] cmd = {"/bin/sh", "-c", "uname -r"};
                Process p = Runtime.getRuntime().exec(cmd);
                kernel = Util.readStream(p.getInputStream());
            }
        } catch (IOException e) {
            log.info("Unable to get kernel info");
            kernel = "N/A";
            return;
        }
        String sys = String.format("%s (%s)", os, kernel);
        String jdk = System.getProperty("java.version");
        String ip;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://checkip.amazonaws.com")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            ip = response.body().string();
        } catch (IOException e) {
            ip = "failed to get IP";
            e.printStackTrace();
        }
        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        long millis = mx.getUptime();

        String uptime = String.format("%dh %dm %ds",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.HOURS.toSeconds(TimeUnit.MILLISECONDS.toHours(millis)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        String mem = String.format("%dMB/%dMB",
                Runtime.getRuntime().totalMemory()/1048576,
                Runtime.getRuntime().maxMemory()/1048576
        );

        JDA jda = Bot.getInstance().getJda();
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Util.blue)
                .setTitle(":information_source: Bot Information")
                .addField("OS", MarkdownUtil.codeblock(sys), true)
                .addField("JDK", MarkdownUtil.codeblock(jdk), true)
                .addField("Uptime", MarkdownUtil.codeblock(uptime), true)
                .addField("JDA Version", MarkdownUtil.codeblock(JDAInfo.VERSION), true)
                .addField("Gateway Ping", MarkdownUtil.codeblock(Bot.getInstance().getJda().getGatewayPing() + "ms"), true)
                .addField("Memory usage", MarkdownUtil.codeblock(mem), true)
                .addField("Servers", MarkdownUtil.codeblock(String.valueOf(jda.getGuildCache().size())), true)
                .addField("Users", MarkdownUtil.codeblock(String.valueOf(jda.getUserCache().size())), true)
                .addField("Commands used", MarkdownUtil.codeblock(String.valueOf(CommandLogger.commandCounter)), true)
                .addField("Developers", MarkdownUtil.codeblock(
                        Bot.getInstance().getDevelopers().stream().map(User::getAsTag).collect(Collectors.joining(", "))), false)
                .setTimestamp(Instant.now())
                .build();
        event.replyEmbeds(embed).queue();
    }
}
