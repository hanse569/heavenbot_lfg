package be.isservers.hmb.command.privateCommand;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lfg.LFGmain;

public class LfgCommand implements ICommand {
    public void handle(CommandContext ctx) {
        LFGmain.privateMessageReceivedEvent(ctx.getPrivateEvent(),ctx.getArgs());
    }

    @Override
    public int getType() {
        return this.PRIVATE_COMMAND;
    }

    @Override
    public String getName() {
        return "lfg";
    }

    @Override
    public String getHelp() {
        return "Permet la création d'un nouvelle evenement qui sera ajouté au canal HeavenBot sur le discord de Heaven";
    }
}
