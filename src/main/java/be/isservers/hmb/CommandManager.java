package be.isservers.hmb;

import be.isservers.hmb.command.IPrivateCommand;
import be.isservers.hmb.command.IPublicCommand;
import be.isservers.hmb.command.PrivateCommandContext;
import be.isservers.hmb.command.PublicCommandContext;
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
    private final List<IPublicCommand> publicCommands = new ArrayList<>();
    private final List<IPrivateCommand> privateCommands = new ArrayList<>();

    CommandManager() {
        addPublicCommand(new PingCommand());
        addPublicCommand(new HelpCommand(this));
        addPublicCommand(new MemeCommand());
        addPublicCommand(new JokeCommand());

        addPublicCommand(new SetPrefixCommand());
        addPublicCommand(new ClearChannelCommand());

        addPublicCommand(new JoinCommand());
        addPublicCommand(new PlayCommand());
        addPublicCommand(new StopCommand());
        addPublicCommand(new SkipCommand());
        addPublicCommand(new NowPlayingCommand());
        addPublicCommand(new QueueCommand());

        addPrivateCommand(new LfgCommand());
    }

    private void addPublicCommand(IPublicCommand cmd){
        boolean nameFound = this.publicCommands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        publicCommands.add(cmd);
    }

    private void addPrivateCommand(IPrivateCommand cmd){
        boolean nameFound = this.privateCommands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        privateCommands.add(cmd);
    }

    public List<IPublicCommand> getPublicCommands(){
        return publicCommands;
    }
    public List<IPrivateCommand> getPrivateCommands(){
        return privateCommands;
    }

    @Nullable
    public IPublicCommand getPublicCommand(String search){
        String searchLower = search.toLowerCase();

        for (IPublicCommand cmd : this.publicCommands) {
            if (cmd.getName().equals(searchLower)
                    || cmd.getAliases().contains(searchLower)){
                return cmd;
            }
        }

        return null;
    }

    @Nullable
    public IPrivateCommand getPrivateCommand(String search){
        String searchLower = search.toLowerCase();

        for (IPrivateCommand cmd : this.privateCommands) {
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
        IPublicCommand cmd = this.getPublicCommand(invoke);

        if (cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            PublicCommandContext ctx = new PublicCommandContext(event, args);

            cmd.handle(ctx);
        }
    }

    void handle(PrivateMessageReceivedEvent event, String prefix){
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix),"")
                .split("\\s+");
        String invoke = split[0].toLowerCase();

        genericHandle(event,invoke,split);
    }

    void handle(PrivateMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split("\\s+");

        genericHandle(event,"lfg",split);
    }

    private void genericHandle(PrivateMessageReceivedEvent event, String invoke, String[] split) {
        IPrivateCommand cmd = this.getPrivateCommand(invoke);

        if (cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            PrivateCommandContext ctx = new PrivateCommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}
