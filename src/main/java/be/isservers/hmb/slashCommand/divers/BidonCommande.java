package be.isservers.hmb.slashCommand.divers;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;

import java.util.concurrent.TimeUnit;

public class BidonCommande extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        System.out.println("Dans test bion");
        ctx.getEvent().reply("Cette commande ne sert qu'a décorer !").queue();
    }

    @Override
    public String getName() {
        return "testbidon";
    }

    @Override
    public String getHelp() {
        return "cette commande permet de clear les commandes globales à l'init";
    }

    @Override
    public int getType() {
        return SlashCommand.GLOBAL_COMMAND;
    }
}
