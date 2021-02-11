package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.OrganizedDate;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class LFGemoteManagement extends ListenerAdapter {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getChannel().getId().equals(Config.getIdChannelHeavenBot())){
            String messageId = event.getMessageId();

            if(!event.getUser().isBot()){
                for (OrganizedDate tmp : LFGdataManagement.listDate){
                    if(tmp.getIdMessageDiscord().equals(messageId)){
                        String emoteReceive = event.getReactionEmote().toString();

                        if(emoteReceive.equals("RE:" + Config.getEmojiTANK())){
                            tmp.addTank(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiHEAL())){
                            tmp.addHeal(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiDPS())){
                            tmp.addDps(event.getUser().getId());
                        }
                        else if(emoteReceive.equals("RE:" + Config.getEmojiDELETE())){
                            tmp.removeRoleList(event.getUser().getId());
                        }
                        event.getChannel().editMessageById(event.getMessageId(),tmp.getEmbedBuilder().build()).complete();//ajoute l'actualisation du message
                        break;
                    }
                }
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
