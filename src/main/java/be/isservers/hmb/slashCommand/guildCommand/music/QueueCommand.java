package be.isservers.hmb.slashCommand.guildCommand.music;

import be.isservers.hmb.Config;
import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.utils.EmoteNumber;
import be.isservers.hmb.utils.MessageUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class QueueCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        final GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();
        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkSelfInVoiceChannel(ctx.getEvent(),selfVoiceState)) return;
        if (!this.checkMemberInVoiceChannel(ctx.getEvent(),memberVoiceState)) return;
        if (!this.checkSameVoiceChannel(ctx.getEvent(),selfVoiceState,memberVoiceState)) return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        final int trackCount = Math.min(queue.size(), 9);
        final ArrayList<AudioTrack> trackList = new ArrayList<>(queue);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("\uD83D\uDCDA Liste d'attente");

        StringBuilder description = new StringBuilder();
        for (int i = 0; i < trackCount; i++) {
            final AudioTrack track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            description.append(EmoteNumber.get(i + 1))
                    .append(" `")
                    .append(info.title)
                    .append(" `\n");
        }
        if (trackList.size() > trackCount) {
            description.append("et `")
                    .append(trackList.size() - trackCount)
                    .append("` autres...");
        }
        eb.setDescription(description.toString());
        ctx.getEvent().replyEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Shows all currently enqueued songs";
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }
}
