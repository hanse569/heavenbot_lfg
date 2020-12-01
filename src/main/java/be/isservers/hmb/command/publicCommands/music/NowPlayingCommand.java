package be.isservers.hmb.command.publicCommands.music;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.lfg.library.MessageUtils;
import be.isservers.hmb.utils.HvmAudioTrack_youtube;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        HvmAudioTrack_youtube audioTrackYoutube = (HvmAudioTrack_youtube) audioPlayer.getPlayingTrack();

        if (audioTrackYoutube == null) {
            channel.sendMessage("Il n'y a pas de lecture actuellement").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(audioTrackYoutube.getInfo().title,audioTrackYoutube.getInfo().uri);
        eb.setDescription("Ajouté par " + audioTrackYoutube.get_hvm_author().getName());
        eb.setThumbnail("https://img.youtube.com/vi/"+audioTrackYoutube.getIdentifier()+"/1.jpg");
        eb.setAuthor("\uD83C\uDFB5 Lecture en cours");
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
