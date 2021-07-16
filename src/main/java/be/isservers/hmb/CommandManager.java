package be.isservers.hmb;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.command.publicCommands.RaiderIO.ProgressCommand;
import be.isservers.hmb.command.publicCommands.admin.*;
import be.isservers.hmb.command.publicCommands.divers.HelpCommand;
import be.isservers.hmb.command.publicCommands.info.TokenCommand;
import be.isservers.hmb.command.publicCommands.info.WorldBossCommand;
import be.isservers.hmb.command.publicCommands.info.WorldEventCommand;
import be.isservers.hmb.command.publicCommands.music.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    CommandManager() {
        addCommand(new ClearChannelCommand());
        addCommand(new PingCommand());
        addCommand(new SetDungeonChannel());
        addCommand(new SetEvanChannel());
        addCommand(new SetGazetteChannel());
        addCommand(new SetPrefixCommand());

        addCommand(new HelpCommand(this));
        addCommand(new be.isservers.hmb.command.publicCommands.divers.LfgCommand());

        addCommand(new TokenCommand());
        addCommand(new WorldBossCommand());
        addCommand(new WorldEventCommand());

        addCommand(new NowPlayingCommand());
        addCommand(new PlayCommand());
        addCommand(new QueueCommand());
        addCommand(new SkipCommand());
        addCommand(new StopCommand());

        addCommand(new ProgressCommand());


        addCommand(new be.isservers.hmb.command.privateCommand.LfgCommand());
    }

    private void addCommand(ICommand cmd){
        boolean nameFound = false;
        for (ICommand command : commands) {
            if (cmd.getType() == command.getType() && cmd.getName().equals(command.getName()))
                nameFound = true;
        }

        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands(){
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search,int TYPE_COMMAND){
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if ((cmd.getName().equals(searchLower)
                    || cmd.getAliases().contains(searchLower))
                && cmd.getType() == TYPE_COMMAND){
                return cmd;
            }
        }

        return null;
    }

    void handle(GuildMessageReceivedEvent event, String prefix){
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix),"")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke,ICommand.PUBLIC_COMMAND);

        if (cmd != null && cmd.getType() == ICommand.PUBLIC_COMMAND){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }

    void handle(PrivateMessageReceivedEvent event, String prefix){
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix),"")
                .split("\\s+");
        String invoke = split[0].toLowerCase();

        ICommand cmd = this.getCommand(invoke,ICommand.PRIVATE_COMMAND);

        if (cmd != null && cmd.getType() == ICommand.PRIVATE_COMMAND){
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }

    void handle(PrivateMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split("\\s+");

        ICommand cmd = this.getCommand("lfg",ICommand.PRIVATE_COMMAND);

        if (cmd != null && cmd.getType() == ICommand.PRIVATE_COMMAND){
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
