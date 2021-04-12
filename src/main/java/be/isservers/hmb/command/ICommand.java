package be.isservers.hmb.command;

import java.util.List;

public interface ICommand {

    int PUBLIC_COMMAND = 1;
    int PRIVATE_COMMAND = 2;

    void handle(CommandContext ctx);

    int getType();

    String getName();

    String getHelp();

    default List<String> getAliases(){
        return List.of();
    }


}
