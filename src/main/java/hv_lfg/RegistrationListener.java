package hv_lfg;

import hv_lfg.library.OrganizedDate;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class RegistrationListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if(event.getChannel().getId().equals("550694482132074506")){
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            MessageEmbed me = message.getEmbeds().get(0);

            if(event.getUser().isBot()){
                String realAuthor = me.getFooter().getText();
                realAuthor = realAuthor.substring("Cree par ".length(),realAuthor.length()-  " - Powered by HeavenBot".length());

                OrganizedDate od = null;
                for (OrganizedDate tmp: Main.waitListDate) {
                    if(tmp.getAdmin().getName().equals(realAuthor)){
                        od = tmp;
                    }
                }
                if(od != null)Main.confirmEvent(od,event.getMessageId());
            }
            else {
                System.out.println(event.getUser().getName() + " a reagi avec " + event.getReactionEmote() + " sur le message " + event.getMessageId());

                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }
    }
}
