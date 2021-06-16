package be.isservers.hmb.slashCommand;

import java.util.List;

public interface ISlashCommand {
    void handle(SlashCommandContext ctx);

    String getName();

    String getHelp();

    default List<SlashCommandParamaterItem> getParam() { return List.of(); }
}
