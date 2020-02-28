package hv_lfg;

import hv_lfg.library.OrganizedDate;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
                        switch (event.getReactionEmote().toString()){
                            case "RE:U+1f6e1":
                                System.out.println("Ajout d un tank dans " + tmp.getIdMessageDiscord());
                                tmp.addTank(event.getUser().getId());
                                break;

                            case "RE:U+1f489":
                                System.out.println("Ajout d un heal dans " + tmp.getIdMessageDiscord());
                                tmp.addHeal(event.getUser().getId());
                                break;

                            case "RE:U+2694":
                                System.out.println("Ajout d un dps dans " + tmp.getIdMessageDiscord());
                                tmp.addDps(event.getUser().getId());
                                break;

                            case "RE:U+274c":
                                System.out.println("Suppresion dans " + tmp.getIdMessageDiscord());
                                tmp.removeRoleList(event.getUser().getId());
                                break;
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
