package be.isservers.hmb.command.publicCommands.divers;

import be.isservers.hmb.CommandManager;
import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        if (args.isEmpty()){
            StringBuilder builder = new StringBuilder();

            builder.append("Liste des commandes\n");

            for (ICommand command : manager.getCommands()) {
                if (command.getType() == ICommand.PUBLIC_COMMAND)
                    builder.append('`')
                            .append(Config.getPrefix())
                            .append(command.getName())
                            .append("`\n");
            }

            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = null;

        if (ctx.getGuildEvent() != null) {
            command = manager.getCommand(search,ICommand.PUBLIC_COMMAND);
        }

        if (ctx.getPrivateEvent() != null) {
            command = manager.getCommand(search,ICommand.PRIVATE_COMMAND);
        }

        if (command == null){
            channel.sendMessage("Rien trouvé pour " + search).queue();
            return;
        }

        if (ctx.getGuildEvent() != null && command.getType() == ICommand.PRIVATE_COMMAND) {
            channel.sendMessage(search + " est une commande réservé au chat privé").queue();
            return;
        }

        if (ctx.getPrivateEvent() != null && command.getType() == ICommand.PUBLIC_COMMAND) {
            channel.sendMessage(search + " est une commande réservé au channel heavenbot / bot-spam").queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Affiche la liste avec les commandes du bot\n" +
                "Usage `!!help [command]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}
