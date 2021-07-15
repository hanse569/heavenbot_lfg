package be.isservers.hmb.slashCommand;

import be.isservers.hmb.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SlashCommand implements ISlashCommand {
    public abstract void handle(SlashCommandContext ctx);

    public List<SlashCommandParamaterItem> getParam() { return List.of(); }

    protected Boolean checkEvanChannel(SlashCommandEvent event, String textChannelId) {
        if (!textChannelId.equals(Config.getIdChannelEvan())){
            this.temporaryMessage(event,":x: Veuillez envoyer votre commande depuis <#" + Config.getIdChannelEvan() + ">",true);
            return false;
        }
        return true;
    }

    protected Boolean checkMemberPermission(SlashCommandEvent event,Member member,Permission permission) {
        return this.checkPermission(
                event,
                member,
                permission,
                ":x: Vous devez avoir l'autorisation " + permission.toString().toUpperCase() + " pour utiliser cette commande"
        );
    }

    protected Boolean checkBotPermission(SlashCommandEvent event,Member selfMember,Permission permission) {
        return this.checkPermission(
                event,
                selfMember,
                permission,
                ":x: J'ai besoin de l'autorisation " + permission.toString().toUpperCase() + " pour utiliser cette commande"
        );
    }

    private Boolean checkPermission(SlashCommandEvent event,Member member,Permission permission,String message) {
        if (!member.hasPermission(permission)){
            this.temporaryMessage(event,message,true);
            return false;
        }
        return true;
    }

    protected Boolean checkMemberInVoiceChannel(SlashCommandEvent event,GuildVoiceState member) {
        return checkUserInVoiceChannel(event,member,":x: Vous devez être dans un canal vocal pour que cette commande fonctionne");
    }

    protected Boolean checkSelfInVoiceChannel(SlashCommandEvent event,GuildVoiceState self) {
        return checkUserInVoiceChannel(event,self,":x: J'ai besoin d'être dans un canal vocal pour que cela fonctionne");
    }

    private Boolean checkUserInVoiceChannel(SlashCommandEvent event,GuildVoiceState member,String message) {
        if (!member.inVoiceChannel()) {
            this.temporaryMessage(event,message,true);
            return false;
        }
        return true;
    }

    protected Boolean checkSameVoiceChannel(SlashCommandEvent event,GuildVoiceState self,GuildVoiceState member) {
        if (!member.getChannel().equals(self.getChannel())) {
            this.temporaryMessage(event,":x: Vous devez être sur le même canal vocal que moi pour que cela fonctionne",true);
            return false;
        }
        return true;
    }

    protected void temporaryMessage(SlashCommandEvent event,String message) {
        temporaryMessage(event,message,false);
    }

    protected void temporaryMessage(SlashCommandEvent event,String message,boolean isEphemeral) {
        if(isEphemeral) {
            event.reply(message).setEphemeral(true).queue();
        }
        else {
            event.reply(message).queue(
                    (originalMessage) -> originalMessage.deleteOriginal().queueAfter(5,TimeUnit.SECONDS)
            );
        }


    }
}
