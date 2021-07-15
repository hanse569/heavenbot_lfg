package be.isservers.hmb.slashCommand.guildCommand.music;

import be.isservers.hmb.Config;
import be.isservers.hmb.lavaplayer.HmbTwitchAudioTrack;
import be.isservers.hmb.lavaplayer.HmbYoutubeAudioTrack;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.utils.MessageUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowPlayingCommand extends SlashCommand {
    @Override
    @SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
    public void handle(SlashCommandContext ctx) {
        final GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkSelfInVoiceChannel(ctx.getEvent(),selfVoiceState)) return;
        if (!this.checkMemberInVoiceChannel(ctx.getEvent(),memberVoiceState)) return;
        if (!this.checkSameVoiceChannel(ctx.getEvent(),selfVoiceState,memberVoiceState)) return;

        final AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

        if (audioPlayer.getPlayingTrack() == null) {
            ctx.getEvent().reply("Il n'y a pas de lecture actuellement").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(audioTrack.getInfo().title,audioTrack.getInfo().uri);
        eb.setAuthor("\uD83C\uDFB5 Lecture en cours");

        if (audioTrack instanceof HmbYoutubeAudioTrack) {
            HmbYoutubeAudioTrack audioTrackYoutube = (HmbYoutubeAudioTrack) audioTrack;
            eb.setDescription("Ajouté par " + audioTrackYoutube.getAuthor().getName());
            eb.setThumbnail("https://img.youtube.com/vi/"+audioTrackYoutube.getIdentifier()+"/1.jpg");
        }
        else if (audioPlayer.getPlayingTrack() instanceof HmbTwitchAudioTrack) {
            HmbTwitchAudioTrack audioTrackTwitch = (HmbTwitchAudioTrack) audioTrack;
            eb.setDescription("Ajouté par " + audioTrackTwitch.getAuthor().getName());
            eb.setThumbnail("https://img.youtube.com/vi/"+audioTrackTwitch.getIdentifier()+"/1.jpg");
        }
        ctx.getEvent().replyEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getHelp() {
        return "Shows the current playing song";
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }
}
