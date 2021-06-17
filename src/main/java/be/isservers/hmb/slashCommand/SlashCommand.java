package be.isservers.hmb.slashCommand;

import be.isservers.hmb.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SlashCommand {
    public static final int GUILD_COMMAND = 1;
    public static final int GLOBAL_COMMAND = 2;

    public abstract void handle(SlashCommandContext ctx);

    public abstract String getName();

    public abstract String getHelp();

    public abstract int getType();

    public List<SlashCommandParamaterItem> getParam() { return List.of(); }

    protected Boolean checkEvanChannel(SlashCommandEvent event, String textChannelId) {
        if (!textChannelId.equals(Config.getIdChannelEvan())){
            event.reply(":x: Veuillez envoyer votre commande depuis <#" + Config.getIdChannelEvan() + ">").queue(
                    (message) -> message.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
            );
            return false;
        }
        return true;
    }

    protected Boolean checkMemberPermission(SlashCommandEvent event,Member member,Permission permission) {
        if (!member.hasPermission(permission)){
            event.reply(":x: Vous devez avoir l'autorisation " + permission.toString().toUpperCase() + " pour utiliser cette commande").queue();
            return false;
        }
        return true;
    }

    protected Boolean checkBotPermission(SlashCommandEvent event,Member selfMember,Permission permission) {
        if (!selfMember.hasPermission(permission)){
            event.reply(":x: J'ai besoin de l'autorisation " + permission.toString().toUpperCase() + " pour utiliser cette commande").queue();
            return false;
        }
        return true;
    }
}
