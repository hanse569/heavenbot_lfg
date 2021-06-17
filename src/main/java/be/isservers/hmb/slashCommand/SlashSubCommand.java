package be.isservers.hmb.slashCommand;

import java.util.ArrayList;
import java.util.List;

public abstract class SlashSubCommand implements ISlashCommand{

    @Override
    public String getHelp() {
        return "decription admin";
    }

    protected List<SlashCommand> listSubCommand = new ArrayList<>();

    public List<SlashCommand> getListSubCommand() {
        return listSubCommand;
    }
}
