package be.isservers.hmb.command.publicCommands.admin;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClearChannelCommand implements ICommand {
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();
        final Member member = ctx.getMember();
        final Member selfMember = ctx.getGuild().getSelfMember();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        if (!member.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.MESSAGE_MANAGE)){
            channel.sendMessage(":x: J'ai besoin de l'autorisation MESSAGE_MANAGE pour utiliser sa commande").queue();
            return;
        }
        
        int amount;
        if (args.isEmpty()) amount = 100;
        else {
            String arg = args.get(0);
            try{
                amount = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                channel.sendMessageFormat("`%s` n'est pas un nombre valide", arg).queue();
                return;
            }
        }

        channel.getIterableHistory()
            .takeAsync(amount+1)
            .thenApplyAsync((messages) -> {
                List<Message> goodMessages = messages.stream()
                    .filter((m) -> !m.getTimeCreated().isAfter(
                            OffsetDateTime.now().plus(2, ChronoUnit.WEEKS)
                    ))
                    .collect(Collectors.toList());

                channel.purgeMessages(goodMessages);

                return goodMessages.size();
            })
            .whenCompleteAsync(
                (count,thr) -> channel.sendMessageFormat("`%d` Messages supprimés",count-1).queue(
                        (message) -> message.delete().queueAfter(10, TimeUnit.SECONDS)
                )
            )
            .exceptionally((throwable) -> {
                String cause;

                if (throwable.getCause() != null) {
                    cause = "Causé par: " + throwable.getCause().getMessage();

                    channel.sendMessageFormat("Erreur: %s%s", throwable.getMessage(),cause).queue();
                }

                return 0;
            });
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "cchannel";
    }

    @Override
    public String getHelp() {
        return "COMMANDE ADMINISTRATEUR: Efface le chat";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cc","cclear");
    }
}
