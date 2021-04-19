package be.isservers.hmb.command.publicCommands.divers;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lfg.LFGmain;
import be.isservers.hmb.utils.MessageUtils;

public class LfgCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        MessageUtils.SendPrivateRichEmbed(ctx.getAuthor(),LFGmain.GenerateLFGHelp());
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "lfg";
    }

    @Override
    public String getHelp() {
        return "redirige vers la conversation privé avec le bot";
    }
}
