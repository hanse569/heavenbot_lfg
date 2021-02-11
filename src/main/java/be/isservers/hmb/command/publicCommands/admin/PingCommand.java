package be.isservers.hmb.command.publicCommands.admin;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class PingCommand implements ICommand {
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        if (!ctx.getMember().hasPermission(Permission.MANAGE_SERVER)){
            ctx.getChannel().sendMessage(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel()
                .sendMessageFormat("Reset ping: %sms\nWS ping: %sms",ping,jda.getGatewayPing()).queue()
        );

    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the discord servers";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
