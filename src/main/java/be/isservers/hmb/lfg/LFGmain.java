package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.MessageUtils;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static be.isservers.hmb.lfg.LFGdataManagement.getInstanceObjectWithOrder;

public class LFGmain extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGmain.class);
    private ArrayList<OrganizedDate> tmpListDate = new ArrayList<>();
    public static int nr = 0;

    @SuppressWarnings({"PlaceholderCountMatchesArgumentCount", "ConstantConditions"})
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        if(event.getJDA().getGuilds().isEmpty()){
            LOGGER.info("This bot is not on any guilds!", event.getJDA().getSelfUser().getAsTag());
        }
        else{
            OrganizedDate.jda = event.getJDA();

            for (Guild guild : event.getJDA().getGuilds()){
                if(guild.getId().equals(Config.getIdDiscordHeaven())){
                    LOGGER.info("Connected to " + guild.getName(), event.getJDA().getSelfUser().getAsTag());
                    clear(guild.getTextChannelById(Config.getIdChannelHeavenBot()));
                    LFGdataManagement.heavenDiscord = guild;
                    LFGdataManagement.InitializeOrganizedDate(event);
                }
            }
        }
    }

    private void clear(TextChannel channel){
        List<Message> messages = channel.getHistory().retrievePast(50).complete();
        if(!(messages.isEmpty() || messages.size() < 2)) {
            LOGGER.info("Deleting messages in the channel " + channel);
            channel.deleteMessages(messages).complete();
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(nr > 0 && !event.getMessage().getContentDisplay().equals(".")  && event.getChannel().getId().equals(Config.getIdChannelHeavenBot()) && event.getAuthor().isBot()){
            OrganizedDate od = LFGdataManagement.listDate.get(LFGdataManagement.listDate.size() - nr);
            od.setIdMessageDiscord(event.getMessageId());
            nr--;
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        OrganizedDate newOD = null;
        for (OrganizedDate obj: tmpListDate) {
            if(obj.getAdminId().equals(user.getId()))
                newOD = obj;
        }

        if(!event.getAuthor().isBot()) {
            if(newOD==null) {
                newOD = new OrganizedDate(user.getId());
                tmpListDate.add(newOD);
            }

            if(newOD.etape == 0){
                if(msg.getContentDisplay().startsWith("!lfg raid")){
                    AfficheListRaid(user);

                    newOD.type = 1;
                    newOD.etape++;
                }
                else if(msg.getContentDisplay().startsWith("!lfg donjon")){
                    AfficheListDonjon(user);

                    newOD.type = 2;
                    newOD.etape++;
                }/*
                else if(msg.getContentDisplay().startsWith("!lfg bg")){
                    tmpDate = new OrganizedDate(new RegisteredMember(user.getId(),user.getName()));
                    this.SendPrivateMessage(user,"**Creation d'un bg**");
                    this.etape = 3;
                    etape++;
                }
                else if(msg.getContentDisplay().startsWith("!lfg arene")){
                    tmpDate = new OrganizedDate(new RegisteredMember(user.getId(),user.getName()));
                    this.SendPrivateMessage(user,"**Creation d'une arene**");
                    this.etape = 4;
                    etape++;
                }*/
                else{
                    MessageUtils.SendPrivateMessage(user);
                }
            }
            else if(newOD.type > 0){
                if(newOD.type == 1 || newOD.type == 2){
                    ProgrammationInstance(event,newOD);
                }
            }

        }
    }

    private void ProgrammationInstance(PrivateMessageReceivedEvent event, OrganizedDate newRaid){
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(newRaid.etape == 1){ //Enregistrement raid et Demande difficulté
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                newRaid.setInstance(getInstanceObjectWithOrder(newRaid.type,val-1));

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            }
            catch(NumberFormatException | NotFoundException ex){
                AfficheListRaid(user);
            }

        } //Enregistrement raid et Demande difficulté
        else if(newRaid.etape == 2){
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                newRaid.setDifficulty(val-1);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement difficulté et Demande date
        else if(newRaid.etape == 3){
            try{
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Date date = df.parse(msg.getContentDisplay());
                newRaid.setDate(date);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregristement date et demande heure
        else if(newRaid.etape == 4){
            try{
                DateFormat df = new SimpleDateFormat("HH:mm");
                Date date = df.parse(msg.getContentDisplay());

                Calendar newDate = Calendar.getInstance();
                newDate.setTime(newRaid.getDateToDate());
                Calendar heure = Calendar.getInstance();
                heure.setTime(date);
                newDate.add(Calendar.HOUR,heure.get(Calendar.HOUR_OF_DAY));
                newDate.add(Calendar.MINUTE,heure.get(Calendar.MINUTE));
                newRaid.setDate(newDate.getTime());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Indique une description: ");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(newRaid.etape == 5){
            newRaid.setDescription(msg.getContentDisplay());

            MessageUtils.SendPrivateRichEmbed(user,newRaid.getEmbedBuilder());
            newRaid.etape++;

            tmpListDate.remove(newRaid);
            LFGdataManagement.addListeEvent(newRaid);

            MessageUtils.SendPublicRichEmbed(event.getJDA(),newRaid);

        } //Enregistrement description
    }

    private void AfficheListRaid(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un raid");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0; i < LFGdataManagement.Raid.size(); i++){
            temp = temp.concat((i+1) + " " + LFGdataManagement.Raid.get(i) + "\n");
        }
        eb.setDescription(temp);
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }

    private void AfficheListDonjon(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un donjon");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0; i < LFGdataManagement.Donjon.size(); i++){
            temp = temp.concat((i+1) + " " + LFGdataManagement.Donjon.get(i) + "\n");
        }
        eb.setDescription(temp);
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }
}
