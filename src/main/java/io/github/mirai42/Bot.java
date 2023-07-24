package io.github.mirai42;

import io.github.mirai42.command.dev.ActivityCommand;
import io.github.mirai42.command.dev.EvalCommand;
import io.github.mirai42.command.dev.ShutdownCommand;
import io.github.mirai42.command.fun.CoinflipCommand;
import io.github.mirai42.command.fun.ColorRoleCommand;
import io.github.mirai42.command.info.BotInfoCommand;
import io.github.mirai42.command.info.ColorCommand;
import io.github.mirai42.command.info.PingCommand;
import io.github.mirai42.command.info.SnowflakeCommand;
import io.github.mirai42.command.misc.HashCommand;
import io.github.mirai42.command.misc.PollCommand;
import io.github.mirai42.command.misc.WolframAlphaCommand;
import io.github.mirai42.command.misc.YoutubeDownload;
import io.github.mirai42.util.CommandLogger;
import io.github.mirai42.util.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class Bot {
    private static Bot instance;
    private JDA jda;
    private Properties config;
    private List<User> developers;

    public static Bot getInstance() {
        return instance;
    }

    private Bot(String configLocation) {
        this.config = new Properties();
        try {
            this.config.load(this.getClass().getClassLoader().getResourceAsStream(configLocation));
        } catch (IOException e) {
            log.info("Could not load config, exiting...");
            e.printStackTrace();
        }

        String token = Objects.requireNonNull(config.getProperty("bot.token"));
        String activityType = Objects.requireNonNull(config.getProperty("bot.activity.type"));
        String activityText = Objects.requireNonNull(config.getProperty("bot.activity.text"));
        log.info("Config loaded...");

        this.jda = JDABuilder.createDefault(token)
                .setEventManager(new AnnotatedEventManager())
                .addEventListeners(new BotInfoCommand(), new PingCommand(), new SnowflakeCommand(), new ActivityCommand(), new HashCommand(), new CoinflipCommand(), new CommandLogger(),
                        new EvalCommand(), new ColorCommand(), new WolframAlphaCommand(), new ColorRoleCommand(), new ShutdownCommand(), new PollCommand(), new YoutubeDownload())
                .setActivity(Activity.of(Activity.ActivityType.valueOf(activityType.toUpperCase()), activityText))
                .enableIntents(List.of(GatewayIntent.MESSAGE_CONTENT))
                .build();

        this.developers = Arrays.stream(this.config.getProperty("bot.devs")
                        .split(","))
                .map(e -> this.jda.retrieveUserById(e).complete())
                .toList();

        String devsTag = this.developers.stream().map(User::getAsTag).collect(Collectors.joining(" "));
        log.info(String.format("Found %d dev users: %s", this.developers.size(), devsTag));

        jda.updateCommands().addCommands(
                Commands.slash("bot-info", "Bot runtime information"),
                Commands.slash("ping", "Bot and gateway latency"),
                Commands.slash("hash", "Generate a hash of a string")
                        .addOptions(
                                new OptionData(OptionType.STRING, "string", "The string to hash", true),
                                new OptionData(OptionType.STRING, "algorithm", "The hash algorithm to use", false)
                                        .addChoices(HashCommand.ALGORITHMS.stream().map(e -> new Command.Choice(e, e)).toList())
                        ),
                Commands.slash("color", "Get information about a color")
                        .addOption(OptionType.STRING, "hex", "Color hex code", true),
//                Commands.slash("color-role", "Gives the user a colored role")
//                                .addOption(OptionType.STRING, "hex", "Color hex code"),
                Commands.slash("snowflake", "Get information on a snowflake")
                        .addOption(OptionType.STRING, "snowflake", "❄", true),
                Commands.slash("coinflip", "Flip a coin")
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("set-activity", "Set the activity of the bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOptions(
                                new OptionData(OptionType.STRING, "text", "Activity text", true),
                                new OptionData(OptionType.STRING, "type", "Activity type", true)
                                        .addChoices(ActivityCommand.activities.stream().map(e -> new Command.Choice(Util.firstLetterCaps(e), e)).toList()),
                                new OptionData(OptionType.STRING, "url", "URL, needed for streaming activity", false)
                        ),
                Commands.slash("wa", "Ask WolframAlpha")
                        .addOption(OptionType.STRING, "input", "Query", true),
                Commands.slash("shutdown", "Shut down the bot"),
                Commands.slash("poll", "Start a poll")
                        .addOption(OptionType.STRING, "title", "Title of the poll", true)
                        .addOption(OptionType.STRING, "option-1", "Option 1", true)
                        .addOption(OptionType.STRING, "option-2", "Option 2", true)
                        .addOption(OptionType.STRING, "option-3", "Option 3", false)
                        .addOption(OptionType.STRING, "option-4", "Option 4", false)
                        .addOption(OptionType.STRING, "option-5", "Option 5", false)
                        .addOption(OptionType.STRING, "option-6", "Option 6", false)
                        .addOption(OptionType.STRING, "option-7", "Option 7", false)
                        .addOption(OptionType.STRING, "option-8", "Option 8", false)
                        .addOption(OptionType.STRING, "option-9", "Option 9", false),
                Commands.slash("yt-download", "Download a video from youtube")
                        .addOptions(
                                new OptionData(OptionType.STRING, "url", "URL of the video", true),
                                new OptionData(OptionType.BOOLEAN, "extract-audio", "Download only audio", false)
                        ),
                Commands.slash("purge", "Bulk delete messages")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOption(OptionType.INTEGER, "amount", "Amount of messages to delete", true)
                        .addOption(OptionType.USER, "user", "Purge only messages from this user", false),
                Commands.slash("eval", "Execute code")
                        .addSubcommands(
                                new SubcommandData("bash", "Execute bash/shell script")
                                        .addOption(OptionType.STRING, "code", "Code to evaluate", true),
                                new SubcommandData("java", "Execute java code")
                                        .addOption(OptionType.STRING, "code", "Code to evaluate", true)
                        )
        ).queue();
    }

    public static void main(String[] args) {
        instance = new Bot("config.properties");
    }
}
