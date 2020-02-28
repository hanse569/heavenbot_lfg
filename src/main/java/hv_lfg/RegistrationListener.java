package hv_lfg;

import hv_lfg.library.OrganizedDate;
import hv_lfg.library.Settings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RegistrationListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getChannel().getId().equals(Settings.getIdChannelHeavenBot())){
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            MessageEmbed me = message.getEmbeds().get(0);
            String messageId = event.getMessageId();

            if(event.getUser().isBot()){ //Permet de relié le message à l'event
                String realAuthor = Objects.requireNonNull(me.getFooter()).getText();
                realAuthor = Objects.requireNonNull(realAuthor).substring("Cree par ".length(),realAuthor.length()-  " - Powered by HeavenBot".length());

                OrganizedDate od = null;
                for (OrganizedDate tmp: Main.waitListDate) if(tmp.getAdmin().equals(realAuthor)) od = tmp;
                if(od != null) Main.confirmEvent(od,messageId);
            }
            else {
                System.out.println(event.getUser().getName() + " a reagi avec " + event.getReactionEmote() + " sur le message " + event.getMessageId());

                for (OrganizedDate tmp : Main.listDate){
                    if(tmp.getIdMessageDiscord().equals(messageId)){
                        String emoteReceive = event.getReactionEmote().toString();

                        if(emoteReceive.equals(Settings.getEmojiTANKforReceive())){
                            System.out.println("Ajout d un tank dans " + tmp.getIdMessageDiscord());
                            tmp.addTank(event.getUser().getId());
                        }
                        else if(emoteReceive.equals(Settings.getEmojiHEALforReceive())){
                            System.out.println("Ajout d un heal dans " + tmp.getIdMessageDiscord());
                            tmp.addHeal(event.getUser().getId());
                        }
                        else if(emoteReceive.equals(Settings.getEmojiDPSforReceive())){
                            System.out.println("Ajout d un dps dans " + tmp.getIdMessageDiscord());
                            tmp.addDps(event.getUser().getId());
                        }
                        else if(emoteReceive.equals(Settings.getEmojiDELETEforReceive())){
                            System.out.println("Suppresion dans " + tmp.getIdMessageDiscord());
                            tmp.removeRoleList(event.getUser().getId());
                        }

                        //ajouter l'actualisation du message
                        event.getChannel().editMessageById(event.getMessageId(),tmp.getEmbedBuilder().build()).complete();
                        break;
                    }
                }

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
