package be.isservers.hmb.command.publicCommands.divers;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.jsonExtract.wowtokenprices.Converter;
import be.isservers.hmb.jsonExtract.wowtokenprices.TokenPriceToday;
import be.isservers.hmb.utils.HttpRequest;

import java.io.IOException;

public class TokenCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        try {
            TokenPriceToday tdt = Converter.fromJsonString(HttpRequest.get("https://wowtokenprices.com/current_prices.json"));
            ctx.getGuildEvent().getMessage().reply("Prix du token: **" + tdt.getEu().getCurrentPrice()  + "** :coin:").queue();
        }
        catch (IOException ex){
        }
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "token";
    }

    @Override
    public String getHelp() {
        return "Permet d'obtenir le cours actuelle du Token WOW en pièce d'or";
    }
}
