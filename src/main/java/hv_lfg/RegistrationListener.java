package hv_lfg;

import hv_lfg.library.OrganizedDate;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RegistrationListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getChannel().getId().equals("550694482132074506")){
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            MessageEmbed me = message.getEmbeds().get(0);
            String messageId = event.getMessageId();

            String realAuthor = me.getFooter().getText();
            realAuthor = realAuthor.substring("Cree par ".length(),realAuthor.length()-  " - Powered by HeavenBot".length());


            if(event.getUser().isBot()){ //Permet de relié le message à l'event
                OrganizedDate od = null;
                for (OrganizedDate tmp: Main.waitListDate) if(tmp.getAdmin().getName().equals(realAuthor)) od = tmp;
                if(od != null) Main.confirmEvent(od,messageId);
            }
            else {
                System.out.println(event.getUser().getName() + " a reagi avec " + event.getReactionEmote() + " sur le message " + event.getMessageId());

                for (OrganizedDate tmp : Main.listDate){
                    if(tmp.getIdMessageDiscord().equals(messageId)){
                        switch (event.getReactionEmote().toString()){
                            case "RE:U+1f6e1":
                                tmp.addTank(realAuthor);
                                break;

                            case "RE:U+1f489":
                                tmp.addHeal(realAuthor);
                                break;

                            case "RE:U+2694":
                                tmp.addDps(realAuthor);
                                break;

                            case "RE:U+274c":
                                tmp.removeRoleList(realAuthor);
                                break;
                        }

                        //ajouter l'actualisation du message
                        event.getChannel().editMessageById(event.getMessageId(),tmp.getEmbedBuilder().build()).queue();
                        break;
                    }
                }

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
