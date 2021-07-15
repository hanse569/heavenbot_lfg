package be.isservers.hmb.slashCommand.guildCommand.divers;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;

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
