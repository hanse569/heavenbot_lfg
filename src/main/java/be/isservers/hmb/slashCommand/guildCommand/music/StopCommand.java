package be.isservers.hmb.slashCommand.guildCommand.music;

import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;

@SuppressWarnings({"ConstantConditions"})
public class StopCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        final GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkSelfInVoiceChannel(ctx.getEvent(),selfVoiceState)) return;
        if (!this.checkMemberInVoiceChannel(ctx.getEvent(),memberVoiceState)) return;
        if (!this.checkSameVoiceChannel(ctx.getEvent(),selfVoiceState,memberVoiceState)) return;

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();

        ctx.getEvent().reply("Le bot music a été arrêté et la file d'attente a été effacée").queue();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Arrête la chanson en cours et efface la file d'attente";
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }


}
