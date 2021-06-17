package be.isservers.hmb.slashCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class SetEvanChannelCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!this.checkMemberPermission(ctx.getEvent(),ctx.getMember(),Permission.MANAGE_SERVER)) return;

        Config.setEvanChannel(ctx.getChannel().getId());

        ctx.getEvent().replyFormat("New channel for E-van has been set to <#%s>",ctx.getChannel().getId()).queue();
    }

    @Override
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getName() {
        return "setevanchannel";
    }

    @Override
    public String getHelp() {
        return "Défini le canal où le E-Van pourra fonctionner";
    }
}
