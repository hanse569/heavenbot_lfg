package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.slashCommand.*;
import be.isservers.hmb.slashCommand.admin.*;
import be.isservers.hmb.slashCommand.info.*;
import be.isservers.hmb.slashCommand.divers.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static be.isservers.hmb.Bot.jda;
import static be.isservers.hmb.lfg.LFGdataManagement.heavenDiscord;

public class SlashCommandManager {
    private final List<SlashCommand> slashCommands = new ArrayList<>();

    public void Init() {
        CommandListUpdateAction listNewCommands = heavenDiscord.updateCommands();

        addCommand(listNewCommands,new TokenCommand());
        addCommand(listNewCommands,new WorldBossCommand());
        addCommand(listNewCommands,new WorldEventCommand());

        addCommand(listNewCommands,new PingCommand());
        addCommand(listNewCommands,new SetEvanChannelCommand());
        addCommand(listNewCommands,new SetDungeonChannelCommand());
        addCommand(listNewCommands,new SetGazetteChannelCommand());

        addCommand(listNewCommands,new HelpCommand(this));

        listNewCommands.queue();


        /*CommandListUpdateAction globalListNewCommands = jda.updateCommands();
        addCommand(globalListNewCommands,new BidonCommande());
        globalListNewCommands.queue();*/
    }

    private void addCommand(CommandListUpdateAction listNewCommands,SlashCommand cmd){
        boolean nameFound = false;
        for (SlashCommand command : slashCommands) {
            if (cmd.getName().equals(command.getName()))
                nameFound = true;
        }

        if (nameFound) throw new IllegalArgumentException("A command with this name is already present");

        CommandData cd = new CommandData(cmd.getName(),cmd.getHelp());
        for (SlashCommandParamaterItem scpi : cmd.getParam()) {
            cd.addOptions(new OptionData(scpi.getOptionType(),scpi.getName(), scpi.getDescription(), scpi.getRequired()));
        }
        listNewCommands.addCommands(cd);
        slashCommands.add(cmd);
    }

    public void handle(SlashCommandEvent event) {
        SlashCommand cmd = this.getCommand(event.getName());

        if (cmd != null){
            SlashCommandContext ctx = new SlashCommandContext(event);
            cmd.handle(ctx);
        }
        else {
            event.reply("Je ne peux pas gérer cette commande pour le moment :(").queue();
        }
    }

    @Nullable
    public SlashCommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (SlashCommand cmd : this.slashCommands) {
            if (cmd.getName().equals(searchLower)){
                return cmd;
            }
        }
        return null;
    }

    public List<SlashCommand> getCommands() {
        return slashCommands;
    }
}
