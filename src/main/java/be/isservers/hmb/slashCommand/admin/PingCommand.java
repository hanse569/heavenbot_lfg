package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class PingCommand extends SlashCommand {
    public void handle(SlashCommandContext ctx) {
        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkMemberPermission(ctx.getEvent(),ctx.getMember(),Permission.MANAGE_SERVER)) return;

        ctx.getJDA().getRestPing().queue(
                (ping) -> ctx.getEvent().replyFormat("Reset ping: %sms\nWS ping: %sms",ping,ctx.getJDA().getGatewayPing()).queue()
        );
    }

    @Override
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getHelp() {
        return "Affiche le ping actuel du bot vers les serveurs Discord ";
    }

    @Override
    public String getName() {
        return "ping";
    }
}
