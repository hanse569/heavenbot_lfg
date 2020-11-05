package be.isservers.hmb.command.commands.music;

import be.isservers.hmb.command.IPublicCommand;
import be.isservers.hmb.command.PublicCommandContext;
import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand implements IPublicCommand {

    @SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
    @Override
    public void handle(PublicCommandContext ctx) {
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

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();

        channel.sendMessage("Le bot music a été arrêté et la file d'attente a été effacée").queue();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Arrête la chanson en cours et efface la file d'attente";
    }
}
