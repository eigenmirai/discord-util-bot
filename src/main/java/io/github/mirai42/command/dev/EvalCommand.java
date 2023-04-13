package io.github.mirai42.command.dev;

import groovy.lang.GroovyShell;
import io.github.mirai42.Bot;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;

public class EvalCommand {
    private String imports;
    private GroovyShell shell;

    public EvalCommand() {
        this.shell = new GroovyShell();
        String[] packageImports = {
                "java.io",
                "java.lang",
                "java.math",
                "java.time",
                "java.util",
                "java.util.concurrent",
                "java.util.stream",
                "net.dv8tion.jda.api",
                "net.dv8tion.jda.api.entities",
                "net.dv8tion.jda.api.entities.impl",
                "net.dv8tion.jda.api.managers",
                "net.dv8tion.jda.api.managers.impl",
                "net.dv8tion.jda.api.utils",
                "net.dv8tion.jda.api.interactions",
        };
        String[] staticImports = {
                "io.github.mirai42.util.Util",
        };

        StringBuffer buf = new StringBuffer();
        for (String e : packageImports) {
            buf.append("import ");
            buf.append(e);
            buf.append(".*;");
        }
        for (String e : staticImports) {
            buf.append("import static ");
            buf.append(e);
            buf.append(".*;");
        }
        this.imports = buf.toString();
    }

    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"eval".equals(event.getName())) return;

        if (!Bot.getInstance().getDevelopers().contains(event.getUser())) {
            event.replyEmbeds(Embeds.errorEmbed("Permission denied: only developers are allowed to use this command")).queue();
            return;
        }

        switch (event.getSubcommandName()) {
            case "bash" -> {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    event.replyEmbeds(Embeds.errorEmbed("Cannot evaluate bash code on windows.")).queue();
                    return;
                }
                String script = Objects.requireNonNull(event.getOption("code")).getAsString();
                event.deferReply().queue();

                String out, version = "bash";
                try {
                    String[] cmd = {"/bin/bash", "-c", script};
                    String[] versionCmd = {"/bin/bash", "-c", "echo $BASH_VERSION"};
                    Process process = Runtime.getRuntime().exec(cmd);
                    Process versionProcess = Runtime.getRuntime().exec(versionCmd);
                    out = Util.readStream(process.getInputStream());
                    version += " " + Util.readStream(versionProcess.getInputStream()).strip();
                } catch (IOException e) {
                    event.replyEmbeds(Embeds.errorEmbed(e.toString())).queue();
                    return;
                }
                event.getHook().sendMessageEmbeds(Embeds.ioEmbed("Code evaluated (bash)", script, out, version)).queue();
            }
            case "java" -> {
                try {
                    String script = Objects.requireNonNull(event.getOption("code")).getAsString();
                    event.deferReply().queue();

                    shell.setProperty("jda", Bot.getInstance().getJda());
                    shell.setProperty("config", Bot.getInstance().getConfig());
                    shell.setProperty("user", event.getUser());
                    shell.setProperty("channel", event.getChannel());
                    shell.setProperty("ping", Bot.getInstance().getJda().getGatewayPing());
                    shell.setProperty("mx", ManagementFactory.getRuntimeMXBean());
                    if (event.isFromGuild()) {
                        shell.setProperty("member", event.getMember());
                        shell.setProperty("guild", event.getGuild());
                    }

                    Object out = shell.evaluate(imports + script);
                    String outString;
                    if (out == null) {
                        outString = "Success (no output)";
                    } else {
                        outString = out.toString();
                    }
                    event.getHook().sendMessageEmbeds(Embeds.ioEmbed("Code evaluated", script, outString,
                            "java " + shell.evaluate("Runtime.version()").toString())).queue();
                } catch (Exception e) {
                    event.replyEmbeds(Embeds.errorEmbed(e.toString())).setEphemeral(true).queue();
                }
            }
        }
    }
}
