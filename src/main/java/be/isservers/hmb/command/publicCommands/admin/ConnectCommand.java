package be.isservers.hmb.command.publicCommands.admin;

import be.isservers.hmb.api.GeneratePassword;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ConnectCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member member = ctx.getMember();

        if (!member.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        String password = GeneratePassword.getPassword();
        ctx.getGuildEvent().getMessage().reply("Mot de passe temporaire: " + MessageUtils.Bold(password)).queue();
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
