package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class SetGazetteChannelCommand implements ISlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)){
            ctx.getEvent().replyFormat(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        Config.setGazetteChannel(ctx.getChannel().getId());

        ctx.getEvent().replyFormat("New channel for Gazette has been set to <#%s>",ctx.getChannel().getId()).queue();
    }

    @Override
    public String getName() {
        return "setgazettechannel";
    }

    @Override
    public String getHelp() {
        return "Défini le canal où E-Van affichera les informations hebdomadaire";
    }
}
