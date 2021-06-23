package be.isservers.hmb.slashCommand.guildCommand.music;

import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;

public class SkipCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        final GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkSelfInVoiceChannel(ctx.getEvent(),selfVoiceState)) return;
        if (!this.checkMemberInVoiceChannel(ctx.getEvent(),memberVoiceState)) return;
        if (!this.checkSameVoiceChannel(ctx.getEvent(),selfVoiceState,memberVoiceState)) return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            ctx.getEvent().reply("Il n'y a pas de lecture actuellement").queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        ctx.getEvent().reply(":fast_forward: Passage à la musique suivante").queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Skips to next song";
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }
}
