package be.isservers.hmb.command.privateCommand;

import be.isservers.hmb.command.IPrivateCommand;
import be.isservers.hmb.command.PrivateCommandContext;
import be.isservers.hmb.lfg.LFGmain;

public class LfgCommand implements IPrivateCommand {
    @Override
    public void handle(PrivateCommandContext ctx) {
        LFGmain.privateMessageReceivedEvent(ctx.getEvent(),ctx.getArgs());
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
