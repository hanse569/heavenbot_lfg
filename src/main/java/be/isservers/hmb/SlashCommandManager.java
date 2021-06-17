package be.isservers.hmb;

import be.isservers.hmb.slashCommand.*;
import be.isservers.hmb.slashCommand.guildCommand.*;
import be.isservers.hmb.slashCommand.guildCommand.divers.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static be.isservers.hmb.lfg.LFGdataManagement.heavenDiscord;

public class SlashCommandManager {
    private final List<SlashCommand> slashCommands = new ArrayList<>();
    private final List<SlashSubCommand> slashSubCommands = new ArrayList<>();
    public void Init() {
        CommandListUpdateAction listNewCommands = heavenDiscord.updateCommands();

        addCommand(listNewCommands,new HelpCommand(this));

        addSubCommand(listNewCommands,new adminCommand());
        addSubCommand(listNewCommands,new infoCommand());

        listNewCommands.queue();


        /*CommandListUpdateAction globalListNewCommands = jda.updateCommands();
        addCommand(globalListNewCommands,new BidonCommande());
        globalListNewCommands.queue();*/
    }

    private void addCommand(CommandListUpdateAction listCommands,SlashCommand cmd){
        if (isCommandExist(cmd.getName())) throw new IllegalArgumentException("A command with this name is already present");

        CommandData cd = new CommandData(cmd.getName(),cmd.getHelp());
        for (SlashCommandParamaterItem scpi : cmd.getParam()) {
            cd.addOptions(new OptionData(scpi.getOptionType(),scpi.getName(), scpi.getDescription(), scpi.getRequired()));
        }
        listCommands.addCommands(cd);
        slashCommands.add(cmd);
    }

    private void addSubCommand(CommandListUpdateAction listCommands,SlashSubCommand cmd){
        CommandData commandData = new CommandData(cmd.getName(),cmd.getHelp());
        for (SlashCommand slashCommand : cmd.getListSubCommand()) {
            try {
                if (isCommandExist(cmd.getName())) throw new IllegalArgumentException("A command with this name is already present");

                SubcommandData scd = new SubcommandData(slashCommand.getName(),slashCommand.getHelp());
                commandData.addSubcommands(scd);
                slashCommands.add(slashCommand);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        slashSubCommands.add(cmd);
        listCommands.addCommands(commandData);
    }

    private Boolean isCommandExist(String name) {
        for (ISlashCommand command : slashCommands) {
            if (name.equals(command.getName()))
                return true;
        }
        return false;
    }

    public void handle(SlashCommandEvent event) {
        SlashCommand cmd = null;
        if (slashSubCommands.stream().anyMatch(slashSubCommand -> slashSubCommand.getName().equals(event.getName()))) {
            cmd = this.getCommand(event.getSubcommandName());
        }
        else {
            cmd = this.getCommand(event.getName());
        }

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
