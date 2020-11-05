package be.isservers.hmb.command.publicCommands;

import be.isservers.hmb.CommandManager;
import be.isservers.hmb.VeryBadDesign;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.command.IPublicCommand;
import be.isservers.hmb.command.PublicCommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements IPublicCommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(PublicCommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()){
            StringBuilder builder = new StringBuilder();
            String prefix = VeryBadDesign.PREFIXES.get(ctx.getGuild().getIdLong());

            builder.append("Liste des commandes\n");

            manager.getPublicCommands().stream().map(ICommand::getName).forEach(
                    (it) -> builder.append('`')
                            .append(prefix)
                            .append(it)
                            .append("`\n")
            );

            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getPublicCommand(search);

        if (command == null){
            channel.sendMessage("Rien trouvé pour " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
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