package be.isservers.hmb.slashCommand.guildCommand.admin;

import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.slashCommand.SlashCommandParamaterItem;
import be.isservers.hmb.slashCommand.SlashCommandParameter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClearChannelCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;
        if (!this.checkMemberPermission(ctx.getEvent(),ctx.getMember(),Permission.MANAGE_SERVER)) return;
        if (!this.checkBotPermission(ctx.getEvent(),ctx.getSelfMember(),Permission.MESSAGE_MANAGE)) return;

        int amount;
        if (args.isEmpty()) amount = 100;
        else {
            String arg = args.get(0);
            try{
                amount = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                ctx.getEvent().replyFormat("`%s` n'est pas un nombre valide", arg).queue();
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
                        (count,thr) -> ctx.getEvent().replyFormat("`%d` Messages supprimés",count-1).queue(
                                (message) -> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                        )
                )
                .exceptionally((throwable) -> {
                    String cause;

                    if (throwable.getCause() != null) {
                        cause = "Causé par: " + throwable.getCause().getMessage();
                        ctx.getEvent().replyFormat("Erreur: %s%s", throwable.getMessage(),cause).queue();
                    }

                    return 0;
                });
    }

    @Override
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getName() {
        return "clearchannel";
    }

    @Override
    public String getHelp() {
        return "COMMANDE ADMINISTRATEUR: Efface le chat";
    }

    @Override
    public List<SlashCommandParamaterItem> getParam() {
        return new SlashCommandParameter().add(OptionType.INTEGER,"quantité","nombre de message à supprimer",false).build();
    }
}
