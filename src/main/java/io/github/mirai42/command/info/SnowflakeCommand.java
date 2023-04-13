package io.github.mirai42.command.info;

import io.github.mirai42.Bot;
import io.github.mirai42.util.Embeds;
import io.github.mirai42.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.time.Instant;
import java.util.Objects;

public class SnowflakeCommand {
    @SubscribeEvent
    public void handleCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel().getType().isMessage() &&
                event.getChannel().getType().isGuild())) return;
        if (!"snowflake".equals(event.getName())) return;

        event.deferReply().queue();

        String snowflakeString = Objects.requireNonNull(event.getOption("snowflake")).getAsString();
        long snowflake;
        try {
            snowflake = Long.parseLong(snowflakeString);
        } catch (Exception e) {
            event.replyEmbeds(Embeds.errorEmbed("This is not a valid snowflake.")).queue();
            return;
        }
        JDA jda = Bot.getInstance().getJda();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Util.blue)
                .setTimestamp(Instant.now());

        Object snowflakeEntity;
        // user
        if ((snowflakeEntity = jda.getUserCache().getElementById(snowflake)) != null) {
            User user = (User) snowflakeEntity;
            String avatar = user.getAvatar().getUrl();
            String name = String.format("**Name:** %s (%s)", user.getAsMention(), user.getAsTag());

            embed.addField("→ User Info", name, false);
            embed.setThumbnail(avatar);
            embed.setTitle(":snowflake: " + user.getAsTag() + " `[User]`");
        }
        // guild
        else if ((snowflakeEntity = jda.getGuildCache().getElementById(snowflake)) != null) {
            Guild guild = (Guild) snowflakeEntity;
            String ownerName;
            if (guild.getOwner() == null) {
                ownerName = "Unknown";
            } else {
                User owner = guild.getOwner().getUser();
                ownerName = String.format("%s (%s)", owner.getAsMention(), owner.getAsTag());
            }

            embed.addField("→ Server Info", String.format("**Name:** %s\n**Owner:** %s\n**Members:** %d",
                    guild.getName(), ownerName, guild.getMemberCount()), false);
            embed.setThumbnail(guild.getIconUrl());
            embed.setTitle(":snowflake: " + guild.getName() + " `[Server]`");
        }
        // channel
        else if ((snowflakeEntity = jda.getTextChannelCache().getElementById(snowflake)) != null) {
            TextChannel channel = (TextChannel) snowflakeEntity;
            String name = channel.getName();
            embed.addField("→ Channel Info", String.format("**Type:** %s\n**Name:** #%s\n**Category:** %s\n**NSFW:** %s",
                    channel.getType(), channel.getName(), channel.getParentCategory().getName(), channel.isNSFW()), false);
            embed.setTitle(":snowflake: " + channel.getName() + " `[Channel]`");
        }
        // dm
        else if ((snowflakeEntity = jda.getPrivateChannelCache().getElementById(snowflake)) != null) {
            PrivateChannel channel = (PrivateChannel) snowflakeEntity;
            User recipient = channel.getUser();
            embed.addField("→ Channel Info", String.format("**Type:** DM\n**Recipient:** %s", recipient.getAsMention()), false);
            embed.setThumbnail(recipient.getAvatarUrl());
            embed.setTitle(":snowflake: DM with" + recipient.getAsTag() + " `[Channel]`");
        }
        // vc
        else if ((snowflakeEntity = jda.getVoiceChannelCache().getElementById(snowflake)) != null) {
            VoiceChannel channel = (VoiceChannel) snowflakeEntity;
            String name = channel.getName();
            embed.addField("→ Channel Info", String.format("**Type:** %s\n**Name:** %s\n**Category:** %s\n**NSFW:** %s",
                    channel.getType(), channel.getName(), channel.getParentCategory().getName(), channel.isNSFW()), false);
            embed.setTitle(":snowflake: " + channel.getName() + " `[Channel]`");
        }
        // role
        else if ((snowflakeEntity = jda.getRoleCache().getElementById(snowflake)) != null) {
            Role role = (Role) snowflakeEntity;
            String color = String.format("#%02x%02x%02x", role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
            int members = (int) role.getGuild().getMembers().stream().filter(e -> e.getRoles().contains(role)).count();

            embed.addField("→ Role Info", String.format("**Name:** %s (%s)\n**Members:** %d\n**Color:** %s",
                    role.getAsMention(), role.getName(), members, color), false);
            embed.setTitle(":snowflake: " + role.getName() + " `[Role]`");
        }
        // emoji
        else if ((snowflakeEntity = jda.getEmojiCache().getElementById(snowflake)) != null) {
            Emoji emoji = (Emoji) snowflakeEntity;
            embed.addField("→ Emoji Info", String.format("**Name:** %s\n", emoji.getName()), false);
            embed.setTitle(":snowflake: " + emoji.getName() + " `[Emoji]`");
        } else {
            embed.setTitle(":snowflake: Unknown Snowflake");
        }
        embed.addField("→ Snowflake Info", analyzeSnowflake(snowflake), false);
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    private String analyzeSnowflake(long snowflake) {
        long timestamp = (snowflake >> 22) + 1420070400000L; // bits 22-64, add discord epoch
        long workerId = (snowflake & 0x3E0000) >> 17;
        long processId = (snowflake & 0x1F000) >> 12;
        long increment = snowflake & 0xFFF;
        String created = String.format("<t:%d:R>", Instant.ofEpochMilli(timestamp).getEpochSecond());


        return String.format("**Timestamp:** %d\n**Created:** %s\n**Process ID:** %d\n**Worker ID**: %d\n**Increment:** %d",
                timestamp, created, workerId, processId, increment);
    }
}
