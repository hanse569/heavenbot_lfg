package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class SetEvanChannelCommand implements ISlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)){
            ctx.getEvent().replyFormat(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        Config.setEvanChannel(ctx.getChannel().getId());

        ctx.getEvent().replyFormat("New channel for E-van has been set to <#%s>",ctx.getChannel().getId()).queue();
    }

    @Override
    public String getName() {
        return "setevanchannel";
    }

    @Override
    public String getHelp() {
        return "Défini le canal où le E-Van pourra fonctionner";
    }
}
