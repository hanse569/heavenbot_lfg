package be.isservers.hmb.slashCommand.guildCommand;

import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashSubCommand;
import be.isservers.hmb.slashCommand.guildCommand.info.*;

public class infoCommand extends SlashSubCommand {

    public infoCommand() {
        addCommand(new TokenCommand());
        addCommand(new WorldBossCommand());
        addCommand(new WorldEventCommand());
    }

    private void addCommand(SlashCommand cmd) {
        listSubCommand.add(cmd);
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public int getType() {
        return ISlashCommand.GUILD_COMMAND;
    }
}