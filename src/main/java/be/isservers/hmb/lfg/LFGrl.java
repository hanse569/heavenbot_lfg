package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.OrganizedDate;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LFGrl extends ListenerAdapter {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getChannel().getId().equals(Config.getIdChannelHeavenBot())){
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            MessageEmbed me = message.getEmbeds().get(0);
            String messageId = event.getMessageId();

            if(event.getUser().isBot()){ //Permet de relié le message à l'event
                String realAuthor = Objects.requireNonNull(me.getFooter()).getText();
                realAuthor = realAuthor.substring("Cree par ".length(),realAuthor.length()-  " - Powered by HeavenBot".length());

                OrganizedDate od = null;
                for (OrganizedDate tmp: LFGdata.waitListDate) if(tmp.getAdmin().equals(realAuthor)) od = tmp;
                if(od != null) LFGdata.confirmEvent(od,messageId);
            }
            else {
                //System.out.println(event.getUser().getName() + " a reagi avec " + event.getReactionEmote() + " sur le message " + event.getMessageId());

                for (OrganizedDate tmp : LFGdata.listDate){
                    if(tmp.getIdMessageDiscord().equals(messageId)){
                        String emoteReceive = event.getReactionEmote().toString();

                        if(emoteReceive.equals("RE:" + Config.getEmojiTANK())){
                            //System.out.println("Ajout d un tank dans " + tmp.getIdMessageDiscord());
                            tmp.addTank(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiHEAL())){
                            //System.out.println("Ajout d un heal dans " + tmp.getIdMessageDiscord());
                            tmp.addHeal(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiDPS())){
                            //System.out.println("Ajout d un dps dans " + tmp.getIdMessageDiscord());
                            tmp.addDps(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiDELETE())){
                            //System.out.println("Suppresion dans " + tmp.getIdMessageDiscord());
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
