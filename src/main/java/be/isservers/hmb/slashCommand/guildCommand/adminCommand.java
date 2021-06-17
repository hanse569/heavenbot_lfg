package be.isservers.hmb.slashCommand.guildCommand;

import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashSubCommand;
import be.isservers.hmb.slashCommand.guildCommand.admin.*;

public class adminCommand extends SlashSubCommand {

    public adminCommand() {
        addCommand(new PingCommand());
        addCommand(new SetEvanChannelCommand());
        addCommand(new SetDungeonChannelCommand());
        addCommand(new SetGazetteChannelCommand());
    }

    private void addCommand(SlashCommand cmd) {
        listSubCommand.add(cmd);
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public int getType() {
        return ISlashCommand.GUILD_COMMAND;
    }
}
