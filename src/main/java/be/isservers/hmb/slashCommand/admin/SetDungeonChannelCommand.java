package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class SetDungeonChannelCommand implements ISlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)){
            ctx.getEvent().replyFormat(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        Config.setDungeonChannel(ctx.getChannel().getId());

        ctx.getEvent().replyFormat("New channel for dungeon has been set to <#%s>",ctx.getChannel().getId()).queue();
    }

    @Override
    public String getName() {
        return "setdungeonchannel";
    }

    @Override
    public String getHelp() {
        return "Défini le canal où les évenements organisés sont affichés";
    }
}
