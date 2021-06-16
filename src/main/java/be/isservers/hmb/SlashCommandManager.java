package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.slashCommand.ISlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.slashCommand.SlashCommandParamaterItem;
import be.isservers.hmb.slashCommand.admin.PingCommand;
import be.isservers.hmb.slashCommand.admin.SetDungeonChannelCommand;
import be.isservers.hmb.slashCommand.admin.SetEvanChannelCommand;
import be.isservers.hmb.slashCommand.admin.SetGazetteChannelCommand;
import be.isservers.hmb.slashCommand.info.TokenCommand;
import be.isservers.hmb.slashCommand.info.WorldBossCommand;
import be.isservers.hmb.slashCommand.info.WorldEventCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SlashCommandManager {
    private final List<ISlashCommand> slashCommands = new ArrayList<>();

    public void Init() {
        CommandListUpdateAction listNewCommands = LFGdataManagement.heavenDiscord.updateCommands();
        CommandListUpdateAction globalListNewCommands = Bot.jda.updateCommands();

        addCommand(globalListNewCommands,new TokenCommand());
        addCommand(listNewCommands,new WorldBossCommand());
        addCommand(globalListNewCommands,new WorldEventCommand());

        addCommand(listNewCommands,new PingCommand());
        addCommand(listNewCommands,new SetEvanChannelCommand());
        addCommand(listNewCommands,new SetDungeonChannelCommand());
        addCommand(listNewCommands,new SetGazetteChannelCommand());

        listNewCommands.queue();
        globalListNewCommands.queue();
    }

    private void addCommand(CommandListUpdateAction listNewCommands,ISlashCommand cmd){
        boolean nameFound = false;
        for (ISlashCommand command : slashCommands) {
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
        ISlashCommand cmd = this.getCommand(event.getName());

        if (cmd != null){
            SlashCommandContext ctx = new SlashCommandContext(event);
            cmd.handle(ctx);
        }
        else {
            event.reply("Je ne peux pas gérer cette commande pour le moment :(").queue();
        }
    }

    @Nullable
    public ISlashCommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (ISlashCommand cmd : this.slashCommands) {
            if (cmd.getName().equals(searchLower)){
                return cmd;
            }
        }
        return null;
    }
}
