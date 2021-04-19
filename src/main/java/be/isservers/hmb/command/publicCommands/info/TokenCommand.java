package be.isservers.hmb.command.publicCommands.info;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.utils.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

public class TokenCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        long tokenValue = (long)((double)getToken());
        if (tokenValue> 0){
            ctx.getChannel().sendMessage("Prix du token: **" + tokenValue  + "** :coin:").queue();
        }
        else {
            ctx.getChannel().sendMessage(":x:  Impossible d'obtenir cette donnée pour le moment").queue();
        }
    }

    private Object getToken(){
        try {
            Gson gson = new Gson();
            Map<?, ?> map = gson.fromJson(HttpRequest.get("https://wowtokenprices.com/current_prices.json"),Map.class);
            return ((LinkedTreeMap)map.get("eu")).get("current_price");
        } catch (Exception ex) {
            return 0;
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
