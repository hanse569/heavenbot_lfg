package be.isservers.hmb.slashCommand.guildCommand.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.Permission;

public class SetDungeonChannelCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!this.checkMemberPermission(ctx.getEvent(),ctx.getMember(),Permission.MANAGE_SERVER)) return;

        Config.setDungeonChannel(ctx.getChannel().getId());

        ctx.getEvent().replyFormat("New channel for dungeon has been set to <#%s>",ctx.getChannel().getId()).queue();
    }

    @Override
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getName() {
        return "setdungeonchannel";
    }

    @Override
    public String getHelp() {
        return "Défini le canal où les évenements organisés sont affichés";
    }

    @Override
    public Boolean isEnabled() { return false; }
}
