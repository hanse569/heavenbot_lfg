package hv_lfg;

import hv_lfg.library.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateEventListener extends ListenerAdapter {

    private ArrayList<OrganizedDate> tmpListDate = new ArrayList<>();
    private int nr = 0;

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        if(event.getJDA().getGuilds().isEmpty()){
            System.out.println("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
        }
        else{
            OrganizedDate.jda = event.getJDA();

            for (Guild guild : event.getJDA().getGuilds()){
                if(guild.getId().equals("241110646677176320")){
                    System.out.println("Connecte a " + guild.getName());
                    clear(guild.getTextChannelById("550694482132074506"));
                    InitialiseEvent(event,guild);
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

    private void InitialiseEvent(ReadyEvent event,Guild guild){
        try{
            ResultSet rs = bdd.getTable("SELECT id,idMessageDiscord,admin,instance,difficulty,date,description FROM OrganizedDate;");
            while (rs.next()){
                try{
                    OrganizedDate od = new OrganizedDate();
                    od.setId(rs.getInt("id"));
                    od.setAdmin(rs.getString("admin"));
                    od.setInstance(this.getInstanceObjectWithId(rs.getInt("instance")));
                    od.setDifficulty(rs.getInt("difficulty"));
                    od.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(rs.getString("date")));
                    od.setDescription(rs.getString("description"));

                    System.out.println("Event trouve: " + od.toString());

                    Main.listDate.add(od);
                    nr++;
                }
                catch (NotFoundException ex) { ex.toString(); }

            }
            rs.close();

            ResultSet rsTank = bdd.getTable("SELECT * FROM ParticiperTANK;");
            while (rsTank.next()){
                int idEvent = rsTank.getInt("idEvent");
                String idMember = rsTank.getString("idMember");

                for (OrganizedDate od : Main.listDate){
                    if(od.getId() == idEvent) od.addTank(idMember);
                }
            }
            rsTank.close();

            ResultSet rsHeal = bdd.getTable("SELECT * FROM ParticiperHEAL;");
            while (rsHeal.next()){
                int idEvent = rsHeal.getInt("idEvent");
                String idMember = rsHeal.getString("idMember");

                for (OrganizedDate od : Main.listDate){
                    if(od.getId() == idEvent) od.addHeal(idMember);
                }
            }
            rsHeal.close();

            ResultSet rsDps = bdd.getTable("SELECT * FROM ParticiperDPS;");
            while (rsDps.next()){
                int idEvent = rsDps.getInt("idEvent");
                String idMember = rsDps.getString("idMember");

                for (OrganizedDate od : Main.listDate){
                    if(od.getId() == idEvent) od.addDps(idMember);
                }
            }
            rsDps.close();

            Main.TriListInstance();

            for (OrganizedDate od : Main.listDate){
                SendPublicRichEmbed(event.getJDA(),od);
                SendPublicMessage(event.getJDA(),".");
            }

        }
        catch (SQLException | ParseException ex) { ex.printStackTrace(); }
    }

    private Instance getInstanceObjectWithId(int val) throws NotFoundException{
        for (Instance obj : Main.Raid){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        for (Instance obj : Main.Donjon){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        throw new NotFoundException();
    }

    private Instance getInstanceObjectWithOrder(int type,int val) throws NotFoundException{
        Instance instance = null;

        if(type == 1){
            instance = Main.Raid.get(val);
            if(instance != null) return instance;
        }
        else if(type == 2){
            instance = Main.Donjon.get(val);
            if(instance != null) return instance;
        }

        throw new NotFoundException();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(nr > 0 && !event.getMessage().getContentDisplay().equals(".")  && event.getChannel().getId().equals("550694482132074506") && event.getAuthor().isBot()){
            OrganizedDate od = Main.listDate.get(Main.listDate.size() - nr);
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
            System.out.println("Message du bot");
        }
        else {
            System.out.println("Message recu de " +
                    event.getAuthor().getName() + ": " +
                    event.getMessage().getContentDisplay()
            );

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
                newRaid.setInstance(this.getInstanceObjectWithOrder(newRaid.type,val-1));

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");

                String description = "";
                for(int i = 0;i < Main.diff.size();i++){
                    description = description.concat(i + " " + Main.diff.get(i).getName() + "\n");
                }
                eb.setDescription(description);

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

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(newRaid.getDateToDate());
                calendar.add(Calendar.HOUR,date.getHours());
                calendar.add(Calendar.MINUTE,date.getMinutes());
                newRaid.setDate(calendar.getTime());

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

            SendPublicRichEmbed(event.getJDA(),newRaid);

        } //Enregistrement description
    }

    private void SendPrivateMessage(User user){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage("__*Syntaxe: !lfg <raid|donjon|bg|arene>*__").queue() );
    }

    private void SendPrivateRichEmbed(User user, EmbedBuilder embedBuilder){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

    private void SendPublicMessage(JDA jda,String message){
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("241110646677176320")).getTextChannelById("550694482132074506")).sendMessage(message).queue();
    }

    private void SendPublicRichEmbed(JDA jda,OrganizedDate od){
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("241110646677176320")).getTextChannelById("550694482132074506")).sendMessage(od.getEmbedBuilder().build()).queue( (message) ->
        {
            message.addReaction("\uD83D\uDEE1").queue();
            message.addReaction("\uD83D\uDC89").queue();
            message.addReaction("\u2694").queue();
            message.addReaction("\u274C").queue();
        });
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

    private void AfficheListDonjon(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un donjon");
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < Main.Donjon.size();i++){
            temp = temp.concat((i+1) + " " + Main.Donjon.get(i) + "\n");
        }
        eb.setDescription(temp);
        this.SendPrivateRichEmbed(user,eb);
    }
}
