package be.isservers.hmb;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.command.privateCommand.LfgCommand;
import be.isservers.hmb.command.publicCommands.HelpCommand;
import be.isservers.hmb.command.publicCommands.JokeCommand;
import be.isservers.hmb.command.publicCommands.MemeCommand;
import be.isservers.hmb.command.publicCommands.PingCommand;
import be.isservers.hmb.command.publicCommands.admin.ClearChannelCommand;
import be.isservers.hmb.command.publicCommands.admin.SetPrefixCommand;
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
        addCommand(new PingCommand());
        addCommand(new HelpCommand(this));
        addCommand(new MemeCommand());
        addCommand(new JokeCommand());

        addCommand(new SetPrefixCommand());
        addCommand(new ClearChannelCommand());

        addCommand(new JoinCommand());
        addCommand(new PlayCommand());
        //addCommand(new StopCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlayingCommand());
        addCommand(new QueueCommand());

        addCommand(new LfgCommand());
    }

    private void addCommand(ICommand cmd){
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands(){
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (ICommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower)
                    || cmd.getAliases().contains(searchLower)){
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
        ICommand cmd = this.getCommand(invoke);

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

        ICommand cmd = this.getCommand(invoke);

        if (cmd != null && cmd.getType() == ICommand.PRIVATE_COMMAND){
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }

    void handle(PrivateMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split("\\s+");

        ICommand cmd = this.getCommand("lfg");

        if (cmd != null && cmd.getType() == ICommand.PRIVATE_COMMAND){
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
