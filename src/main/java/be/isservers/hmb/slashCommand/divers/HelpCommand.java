package be.isservers.hmb.slashCommand.divers;

import be.isservers.hmb.CommandManager;
import be.isservers.hmb.Config;
import be.isservers.hmb.SlashCommandManager;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.slashCommand.SlashCommandParamaterItem;
import be.isservers.hmb.slashCommand.SlashCommandParameter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class HelpCommand extends SlashCommand {
    private final SlashCommandManager manager;

    public HelpCommand(SlashCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(SlashCommandContext ctx) {
        List<String> args = ctx.getArgs();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;

        if (args.isEmpty()){
            StringBuilder builder = new StringBuilder();

            builder.append("Liste des commandes\n");
            for (SlashCommand command : manager.getCommands()) {
                if (command.getType() == SlashCommand.GUILD_COMMAND)
                    builder.append("`/")
                        .append(command.getName())
                        .append("`\n");
            }

            ctx.getEvent().reply(builder.toString()).queue();
            return;
        }

        String search = args.get(0);
        SlashCommand command = manager.getCommand(search);;

        if (command == null){
            ctx.getEvent().reply("Rien trouvé pour " + search).queue();
            return;
        }

        ctx.getEvent().reply(command.getHelp()).queue();
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Affiche la liste des commandes de E-van";
    }

    @Override
    public List<SlashCommandParamaterItem> getParam() {
        SlashCommandParameter scp = new SlashCommandParameter();
        scp.add(OptionType.STRING,"commande","Nom de la commande à chercher",false);
        return scp.build();
    }
}
