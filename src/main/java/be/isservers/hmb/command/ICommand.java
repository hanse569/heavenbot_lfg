package be.isservers.hmb.command;

import java.util.List;

public interface ICommand {

    String getName();

    String getHelp();

    default List<String> getAliases(){
        return List.of();
    }
}
