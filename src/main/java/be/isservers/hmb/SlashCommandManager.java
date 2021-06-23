package be.isservers.hmb;

import be.isservers.hmb.slashCommand.*;
import be.isservers.hmb.slashCommand.guildCommand.admin.*;
import be.isservers.hmb.slashCommand.guildCommand.divers.*;
import be.isservers.hmb.slashCommand.guildCommand.info.*;
import be.isservers.hmb.slashCommand.guildCommand.music.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static be.isservers.hmb.lfg.LFGdataManagement.heavenDiscord;

public class SlashCommandManager {
    private final List<SlashCommand> slashCommands = new ArrayList<>();
    public void Init() {
        CommandListUpdateAction guildCommands = heavenDiscord.updateCommands();

        addCommand(guildCommands,new HelpCommand(this));
        addCommand(guildCommands,new LfgCommand());

        addCommand(guildCommands,new ClearChannelCommand());
        addCommand(guildCommands,new PingCommand());
        addCommand(guildCommands,new SetEvanChannelCommand());
        addCommand(guildCommands,new SetDungeonChannelCommand());
        addCommand(guildCommands,new SetGazetteChannelCommand());

        addCommand(guildCommands,new TokenCommand());
        addCommand(guildCommands,new WorldBossCommand());
        addCommand(guildCommands,new WorldEventCommand());

        addCommand(guildCommands,new PlayCommand());
        addCommand(guildCommands,new StopCommand());
        addCommand(guildCommands,new SkipCommand());
        addCommand(guildCommands,new NowPlayingCommand());
        addCommand(guildCommands,new QueueCommand());

        guildCommands.queue();


        /*CommandListUpdateAction globalCommands = Bot.jda.updateCommands();
        globalCommands.queue();*/
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

    private Boolean isCommandExist(String name) {
        for (ISlashCommand command : slashCommands) {
            if (name.equals(command.getName()))
                return true;
        }
        return false;
    }

    public void handle(SlashCommandEvent event) {
        SlashCommand cmd = this.getCommand(event.getName());;
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
