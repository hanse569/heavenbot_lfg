package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.Instance;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import be.isservers.hmb.lfg.library.bdd;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static be.isservers.hmb.lfg.LFGdata.getInstanceObjectWithId;
import static be.isservers.hmb.lfg.LFGdata.getInstanceObjectWithOrder;

public class LFGcel extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGcel.class);
    private ArrayList<OrganizedDate> tmpListDate = new ArrayList<>();
    private int nr = 0;

    @SuppressWarnings("PlaceholderCountMatchesArgumentCount")
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
                    clear(Objects.requireNonNull(guild.getTextChannelById(Config.getIdChannelHeavenBot())));
                    LFGdata.heavenDiscord = guild;
                    InitialiseEvent(event);
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

    private void InitialiseEvent(ReadyEvent event){
        try{
            ResultSet rs = bdd.getTable("SELECT id,idMessageDiscord,admin,instance,difficulty,date,description FROM LFG_OrganizedDate;");
            while (rs.next()){
                try{
                    OrganizedDate od = new OrganizedDate();
                    od.setId(rs.getInt("id"));
                    od.setAdmin(rs.getString("admin"));
                    od.setInstance(getInstanceObjectWithId(rs.getInt("instance")));
                    od.setDifficulty(rs.getInt("difficulty"));
                    od.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(rs.getString("date")));
                    od.setDescription(rs.getString("description"));

                    LOGGER.info("Event finds: " + od.toString(), event.getJDA().getSelfUser().getAsTag());

                    LFGdata.listDate.add(od);
                    nr++;
                }
                catch (NotFoundException ex) { ex.printStackTrace();}

            }
            rs.close();

            ResultSet rsTank = bdd.getTable("SELECT * FROM LFG_ParticiperTANK;");
            while (rsTank.next()){
                int idEvent = rsTank.getInt("idEvent");
                String idMember = rsTank.getString("idMember");

                for (OrganizedDate od : LFGdata.listDate){
                    if(od.getId() == idEvent) od.addTankToList(idMember);
                }
            }
            rsTank.close();

            ResultSet rsHeal = bdd.getTable("SELECT * FROM LFG_ParticiperHEAL;");
            while (rsHeal.next()){
                int idEvent = rsHeal.getInt("idEvent");
                String idMember = rsHeal.getString("idMember");

                for (OrganizedDate od : LFGdata.listDate){
                    if(od.getId() == idEvent) od.addHealToList(idMember);
                }
            }
            rsHeal.close();

            ResultSet rsDps = bdd.getTable("SELECT * FROM LFG_ParticiperDPS;");
            while (rsDps.next()){
                int idEvent = rsDps.getInt("idEvent");
                String idMember = rsDps.getString("idMember");

                for (OrganizedDate od : LFGdata.listDate){
                    if(od.getId() == idEvent) od.addDpsToList(idMember);
                }
            }
            rsDps.close();

            LFGdata.TriListInstance();

            for (OrganizedDate od : LFGdata.listDate){
                SendPublicRichEmbed(event.getJDA(),od);
            }

        }
        catch (SQLException | ParseException ex) { ex.printStackTrace(); }
    }





    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(nr > 0 && !event.getMessage().getContentDisplay().equals(".")  && event.getChannel().getId().equals(Config.getIdChannelHeavenBot()) && event.getAuthor().isBot()){
            OrganizedDate od = LFGdata.listDate.get(LFGdata.listDate.size() - nr);
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

        if(event.getAuthor().isBot()) {
            //System.out.println("Message du bot");
        }
        else {
            /*System.out.println("Message recu de " +
                    event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay()
            );*/

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
                    this.SendPrivateMessage(user);
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

                this.SendPrivateRichEmbed(user,eb);

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

                this.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                this.SendPrivateRichEmbed(user,eb);
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
                this.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                this.SendPrivateRichEmbed(user,eb);
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
                this.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                this.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(newRaid.etape == 5){
            newRaid.setDescription(msg.getContentDisplay());

            this.SendPrivateRichEmbed(user,newRaid.getEmbedBuilder());
            newRaid.etape++;

            tmpListDate.remove(newRaid);
            LFGdata.addListeEvent(newRaid);

            SendPublicRichEmbed(event.getJDA(),newRaid);

        } //Enregistrement description
    }

    private void SendPrivateMessage(User user){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage("__*Syntaxe: !lfg <raid|donjon|bg|arene>*__").queue() );
    }

    private void SendPrivateRichEmbed(User user, EmbedBuilder embedBuilder){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

    @SuppressWarnings("ConstantConditions")
    private static void SendPublicMessage(JDA jda, String message){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()).sendMessage(message).queue();
    }

    @SuppressWarnings("ConstantConditions")
    private void SendPublicRichEmbed(JDA jda,OrganizedDate od){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()).sendMessage(od.getEmbedBuilder().build()).queue( (message) ->
        {
            message.addReaction(Config.getEmojiTANK()).queue();
            message.addReaction(Config.getEmojiHEAL()).queue();
            message.addReaction(Config.getEmojiDPS()).queue();
            message.addReaction(Config.getEmojiDELETE()).queue();
        });
    }

    private void AfficheListRaid(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un raid");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < LFGdata.Raid.size();i++){
            temp = temp.concat((i+1) + " " + LFGdata.Raid.get(i) + "\n");
        }
        eb.setDescription(temp);
        this.SendPrivateRichEmbed(user,eb);
    }

    private void AfficheListDonjon(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un donjon");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < LFGdata.Donjon.size();i++){
            temp = temp.concat((i+1) + " " + LFGdata.Donjon.get(i) + "\n");
        }
        eb.setDescription(temp);
        this.SendPrivateRichEmbed(user,eb);
    }
}
