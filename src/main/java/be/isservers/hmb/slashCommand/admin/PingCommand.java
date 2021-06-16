package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.TimeUnit;

public class PingCommand implements ISlashCommand {
    public void handle(SlashCommandContext ctx) {
        JDA jda = ctx.getJDA();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            ctx.getEvent().reply(":x: Veuillez envoyer votre commande depuis <#" + Config.getIdChannelEvan() + ">").queue(
                    (message) -> message.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
            );
            return;
        }

        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)){
            ctx.getEvent().replyFormat(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        jda.getRestPing().queue(
                (ping) -> ctx.getEvent().replyFormat("Reset ping: %sms\nWS ping: %sms",ping,jda.getGatewayPing()).queue()
        );
    }

    @Override
    public String getHelp() {
        return "Affiche le ping actuel du bot vers les serveurs Discord ";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
