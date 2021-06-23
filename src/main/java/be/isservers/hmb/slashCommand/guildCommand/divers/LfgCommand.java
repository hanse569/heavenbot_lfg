package be.isservers.hmb.slashCommand.guildCommand.divers;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.utils.MessageUtils;

import static be.isservers.hmb.lfg.LFGmain.GenerateLFGHelp;

public class LfgCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        MessageUtils.SendPrivateRichEmbed(ctx.getAuthor(),GenerateLFGHelp());
        this.temporaryMessage(ctx.getEvent(),"Direction la conversation privé :grinning:",true);
    }

    @Override
    public String getName() {
        return "lfg";
    }

    @Override
    public String getHelp() {
        return "Redirige vers la conversation privé avec le bot";
    }

    @Override
    public int getType() {
        return SlashCommand.GLOBAL_COMMAND;
    }
}
