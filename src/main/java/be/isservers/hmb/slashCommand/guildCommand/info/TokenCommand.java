package be.isservers.hmb.slashCommand.guildCommand.info;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.utils.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

public class TokenCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;

        long tokenValue = (long)((double)getToken());
        if (tokenValue> 0){
            ctx.getEvent().reply("Prix du token: **" + tokenValue  + "** :coin:").queue();
        }
        else {
            ctx.getEvent().reply(":x:  Impossible d'obtenir cette donnée pour le moment").queue();
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
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getName() {
        return "token";
    }

    @Override
    public String getHelp() {
        return "Indique le cours actuelle du Token WOW en pièce d'or";
    }
}
