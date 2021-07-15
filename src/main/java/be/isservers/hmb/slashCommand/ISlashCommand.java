package be.isservers.hmb.slashCommand;

public interface ISlashCommand {
    int GUILD_COMMAND = 1;
    int GLOBAL_COMMAND = 2;

    String getName();

    String getHelp();

    default Boolean isEnabled() { return true; };

    int getType();
}
