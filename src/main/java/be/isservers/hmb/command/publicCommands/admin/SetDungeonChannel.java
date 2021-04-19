package be.isservers.hmb.command.publicCommands.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SetDungeonChannel implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member member = ctx.getMember();

        if (!member.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage("You must have the MANAGE_SERVER permission to use his command").queue();
            return;
        }

        Config.setDungeonChannel(ctx.getChannel().getId());

        channel.sendMessageFormat("New channel for dungeon has been set to `%s`",ctx.getChannel().getName()).queue();
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "setdungeonchannel";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("setdonjon");
    }
}
