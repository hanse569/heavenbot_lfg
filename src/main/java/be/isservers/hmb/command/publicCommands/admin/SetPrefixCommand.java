package be.isservers.hmb.command.publicCommands.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SetPrefixCommand implements ICommand {
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();
        final Member member = ctx.getMember();

        if (!member.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage("You must have the MANAGE_SERVER permission to use his command").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("Missing args").queue();
            return;
        }

        final String newPrefix = String.join("", args);
        Config.setPrefix(newPrefix);

        channel.sendMessageFormat("New prefix has been set to `%s`",newPrefix).queue();
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "setprefix";
    }

    @Override
    public String getHelp() {
        return "COMMANDE ADMINISTRATEUR: Définit le préfixe de ce serveur\n" +
                "Usage: `!!setprefix <prefix>`";
    }
}
