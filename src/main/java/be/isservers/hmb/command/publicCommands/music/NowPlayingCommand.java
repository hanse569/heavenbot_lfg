package be.isservers.hmb.command.publicCommands.music;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.HmbTwitchAudioTrack;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.lavaplayer.HmbYoutubeAudioTrack;
import be.isservers.hmb.utils.MessageUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class NowPlayingCommand implements ICommand {
    @SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage(":x: J'ai besoin d'être dans un canal vocal pour que cela fonctionne").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":x: Vous devez être dans un canal vocal pour que cette commande fonctionne").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage(":x: Vous devez être sur le même canal vocal que moi pour que cela fonctionne").queue();
            return;
        }

        final AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;
        final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

        if (audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage("Il n'y a pas de lecture actuellement").queue();
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
        MessageUtils.SendPublicRichEmbed(channel,eb.build());
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getHelp() {
        return "Affiche la chanson en cours de lecture";
    }

    @Override
    public List<String> getAliases() {
        return List.of("now", "np");
    }
}
