package be.isservers.hmb.command.commands.admin;

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
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();
        final Member member = ctx.getMember();
        final Member selfMember = ctx.getGuild().getSelfMember();

        if (!member.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage(":x: Vous devez avoir l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.MANAGE_SERVER)){
            channel.sendMessage(":x: J'ai besoin de l'autorisation MANAGE_SERVER pour utiliser sa commande").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage(":x: L'utilisation correcte est `!!"+getName()+" <amount>`").queue();
            return;
        }

        int amount;
        String arg = args.get(0);

        try{
            amount = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            channel.sendMessageFormat("`%s` n'est pas un nombre valide", arg).queue();
            return;
        }

        channel.getIterableHistory()
            .takeAsync(amount)
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
                (count,thr) -> channel.sendMessageFormat("Messages `%d` supprimés",count).queue(
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
    public String getName() {
        return "cchannel";
    }

    @Override
    public String getHelp() {
        return "COMMANDE ADMINISTRATEUR: Efface le chat";
    }
}
