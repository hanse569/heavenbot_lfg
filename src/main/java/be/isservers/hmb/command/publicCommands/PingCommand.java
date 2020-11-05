package be.isservers.hmb.command.publicCommands;

import be.isservers.hmb.command.IPublicCommand;
import be.isservers.hmb.command.PublicCommandContext;
import net.dv8tion.jda.api.JDA;

public class PingCommand implements IPublicCommand {
    @Override
    public void handle(PublicCommandContext ctx) {
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel()
                .sendMessageFormat("Reset ping: %sms\nWS ping: %sms",ping,jda.getGatewayPing()).queue()
        );

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
