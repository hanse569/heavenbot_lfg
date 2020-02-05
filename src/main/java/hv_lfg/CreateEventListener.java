package hv_lfg;

import hv_lfg.library.OrganizedDate;
import hv_lfg.library.RegisteredMember;
import hv_lfg.library.bdd;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateEventListener extends ListenerAdapter {

    private ArrayList<OrganizedDate> tmpListDate = new ArrayList<>();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        if(event.getJDA().getGuilds().isEmpty()){
            System.out.println("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
        }
        else{
            for (Guild guild : event.getJDA().getGuilds()){
                if(guild.getId().equals("241110646677176320")){
                    System.out.println("Connecte a " + guild.getName());
                    clear(guild.getTextChannelById("550694482132074506"));
                    //lecture des raids
                    InitialiseEvent(guild);
                }
            }
        }
    }

    private void clear(TextChannel channel){
        List<Message> messages = channel.getHistory().retrievePast(50).complete();
        if(messages.isEmpty() || messages.size() < 2) return;
        else{
            System.out.println("Suppresion des messages dans le channel: " + channel);
            channel.deleteMessages(messages).complete();
        }
    }

    private static void InitialiseEvent(Guild guild){
        Connection conn = bdd.getConn();
        ResultSet rs = bdd.getTable(conn,"SELECT idMessageDiscord,admin,instance,difficulty,date,descritption FROM OrganizedDate;");
        try{
            while (rs.next()){
                OrganizedDate od = new OrganizedDate();
                od.setAdmin(new RegisteredMember(rs.getString("admin"),guild.getMemberById(rs.getString("admin")).getNickname()));
                od.setIdMessageDiscord(rs.getString("idMessageDiscord"));
                od.setInstance(rs.getInt("instance"));
                od.setDifficulty(rs.getInt("difficulty"));
                od.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(rs.getString("date")));

                System.out.println(od.toString());
            }
            rs.close();
            conn.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
        catch (ParseException e) { e.printStackTrace(); }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        OrganizedDate newRaid = null;
        for (OrganizedDate obj: tmpListDate) {
            if(obj.getAdmin().getIdDiscord().equals(user.getId()))
                newRaid = obj;
        }

        if(event.getAuthor().isBot()) {
            System.out.println("Message du bot");
            /*if(etape == 6 && type == 1){
                event.getJDA().getGuildById("241110646677176320").getTextChannelById("550694482132074506").sendMessage(event.getMessage()).queue( (message) ->
                {
                    message.addReaction("\uD83D\uDEE1").queue();
                    message.addReaction("\uD83D\uDC89").queue();
                    message.addReaction("\u2694").queue();
                    message.addReaction("\u274C").queue();
                });
            }*/
        }
        else {
            System.out.println("Message recu de " +
                    event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay()
            );

            if(newRaid==null) {
                newRaid = new OrganizedDate(new RegisteredMember(user.getId(),user.getName()));
                tmpListDate.add(newRaid);
            }

            if(newRaid.etape == 0){
                if(msg.getContentDisplay().startsWith("!lfg raid")){
                    AfficheListRaid(user);

                    newRaid.type = 1;
                    newRaid.etape++;
                }
                /*else if(msg.getContentDisplay().startsWith("!lfg donjon")){
                    tmpDate = new OrganizedDate(new RegisteredMember(user.getId(),user.getName()));
                    this.SendPrivateMessage(user,"**Creation d'un donjon**");
                    this.etape = 2;
                    etape++;
                }
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
            else if(newRaid.etape > 0){
                if(newRaid.type == 1){//type = raid
                    ProgrammationRaid(event,newRaid);
                }
            }

        }
    }

    private void ProgrammationRaid(PrivateMessageReceivedEvent event, OrganizedDate newRaid){
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(newRaid.etape == 1){ //Enregistrement raid et Demande difficulté
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                newRaid.setInstance(val-1);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");

                this.SendPrivateRichEmbed(user,eb);

                newRaid.etape++;
            }
            catch(NumberFormatException ex){
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
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            df.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
            Date date;
            try{
                date = df.parse(msg.getContentDisplay());
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
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.FRANCE);
            //df.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
            Date date;
            try{
                date = df.parse(msg.getContentDisplay());
                newRaid.setDate(new Date(newRaid.getDateToDate().getTime() + date.getTime()));

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
            Main.addListeEvent(newRaid);

            Objects.requireNonNull(Objects.requireNonNull(event.getJDA().getGuildById("241110646677176320")).getTextChannelById("550694482132074506")).sendMessage(newRaid.getEmbedBuilder().build()).queue( (message) ->
            {
                message.addReaction("\uD83D\uDEE1").queue();
                message.addReaction("\uD83D\uDC89").queue();
                message.addReaction("\u2694").queue();
                message.addReaction("\u274C").queue();
            });

        } //Enregistrement description
    }

    private void SendPrivateMessage(User user){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage("__*Syntaxe: !lfg <raid|donjon|bg|arene>*__").queue() );
    }

    private void SendPrivateRichEmbed(User user, EmbedBuilder embedBuilder){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

    private void AfficheListRaid(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un raid");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < Main.Raid.size();i++){
            temp = temp.concat((i+1) + " " + Main.Raid.get(i) + "\n");
        }
        eb.setDescription(temp);
        this.SendPrivateRichEmbed(user,eb);
    }
}