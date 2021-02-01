package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.EmptyArrayException;
import be.isservers.hmb.lfg.library.MessageUtils;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import be.isservers.hmb.utils.EmoteNumber;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
import static be.isservers.hmb.lfg.LFGdataManagement.getOrganizedDateByUser;

public class LFGmain extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGmain.class);
    private static final ArrayList<EditEvent> listEventEdited = new ArrayList<>();
    static int nr = 0;

    public static void Clear(TextChannel channel){
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

    public static void  privateMessageReceivedEvent(PrivateMessageReceivedEvent ctx, List<String> args) {
        EditEvent ee = null;
        for (EditEvent obj: listEventEdited) {
            if(obj.getOd().getAdminId().equals(ctx.getAuthor().getId()))
                ee = obj;
        }

        if (ee == null) {
            if (args.size() > 0) {
                if(args.get(0).startsWith("raid")){
                    ee = new EditEvent(EditEvent.ADD,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    AfficheListRaid(ctx.getAuthor());

                    ee.type = 1;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("donjon")){
                    ee = new EditEvent(EditEvent.ADD,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    AfficheListDonjon(ctx.getAuthor());

                    ee.type = 2;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("jcj")){
                    ee = new EditEvent(EditEvent.ADD,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    AfficheListJCJ(ctx.getAuthor());

                    ee.type = 3;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("delete")){
                    ee = new EditEvent(EditEvent.DELETE,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    try {
                        AfficheListEventCreateByUser(ctx.getAuthor());
                        ee.etape++;
                    }
                    catch (EmptyArrayException ex) {
                        MessageUtils.SendPrivateMessage(ctx.getAuthor(),":x: Aucun event n'a pu être trouvé, annulation de la suppression");
                        listEventEdited.remove(ee);
                    }
                }
                else {
                    MessageUtils.SendPrivateMessage(ctx.getAuthor(),"__*Syntaxe: !lfg <raid|donjon|jcj>*__");
                }
            }
            else{
                MessageUtils.SendPrivateMessage(ctx.getAuthor(),"__*Syntaxe: !lfg <raid|donjon|jcj>*__");
            }
        }
        else
        {
            if (ee.getAction() == EditEvent.ADD){
                if(ee.type == 1 || ee.type == 2){
                    ProgrammationPVE(ctx,ee);
                }
                else if(ee.type == 3) {
                    ProgrammationPVP(ctx,ee);
                }
            }
            else if(ee.getAction() == EditEvent.DELETE){
                SuppressionEvent(ctx,ee);
            }
        }
    }

    private static void ProgrammationPVE(PrivateMessageReceivedEvent event, EditEvent ee){
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(ee.etape == 1){ //Enregistrement raid et Demande difficulté
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.getOd().setInstance(getInstanceObjectWithOrder(ee.type,val-1));

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException | NotFoundException ex){
                AfficheListRaid(user);
            }

        } //Enregistrement raid et Demande difficulté
        else if(ee.etape == 2){
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.getOd().setDifficulty(val-1);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement difficulté et Demande date
        else if(ee.etape == 3){
            try{
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Date date = df.parse(msg.getContentDisplay());
                ee.getOd().setDate(date);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregristement date et demande heure
        else if(ee.etape == 4){
            try{
                DateFormat df = new SimpleDateFormat("HH:mm");
                Date date = df.parse(msg.getContentDisplay());

                Calendar newDate = Calendar.getInstance();
                newDate.setTime(ee.getOd().getDateToDate());
                Calendar heure = Calendar.getInstance();
                heure.setTime(date);
                newDate.add(Calendar.HOUR,heure.get(Calendar.HOUR_OF_DAY));
                newDate.add(Calendar.MINUTE,heure.get(Calendar.MINUTE));
                ee.getOd().setDate(newDate.getTime());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Indique une description: ");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(ee.etape == 5){
            ee.getOd().setDescription(msg.getContentDisplay());

            MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
            ee.etape++;

            listEventEdited.remove(ee);
            LFGdataManagement.addListeEvent(ee.getOd());

            MessageUtils.SendPublicRichEmbedPVE(event.getJDA(),ee.getOd());

        } //Enregistrement description
    }

    private static void ProgrammationPVP(PrivateMessageReceivedEvent event, EditEvent ee) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(ee.etape == 1){ //Enregistrement raid et Demande difficulté
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.getOd().setInstance(getInstanceObjectWithOrder(ee.type,val-1));

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Non Coté\n2 Coté");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException | NotFoundException ex){
                AfficheListJCJ(user);
            }

        } //Enregistrement raid et Demande difficulté
        else if(ee.etape == 2){
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.getOd().setDifficulty(val-1);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement difficulté et Demande date
        else if(ee.etape == 3){
            try{
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Date date = df.parse(msg.getContentDisplay());
                ee.getOd().setDate(date);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");

                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregristement date et demande heure
        else if(ee.etape == 4){
            try{
                DateFormat df = new SimpleDateFormat("HH:mm");
                Date date = df.parse(msg.getContentDisplay());

                Calendar newDate = Calendar.getInstance();
                newDate.setTime(ee.getOd().getDateToDate());
                Calendar heure = Calendar.getInstance();
                heure.setTime(date);
                newDate.add(Calendar.HOUR,heure.get(Calendar.HOUR_OF_DAY));
                newDate.add(Calendar.MINUTE,heure.get(Calendar.MINUTE));
                ee.getOd().setDate(newDate.getTime());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Indique une description: ");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(ee.etape == 5){
            ee.getOd().setDescription(msg.getContentDisplay());

            MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
            ee.etape++;

            listEventEdited.remove(ee);
            LFGdataManagement.addListeEvent(ee.getOd());

            MessageUtils.SendPublicRichEmbedPVP(event.getJDA(),ee.getOd());

        } //Enregistrement description
    }

    private static void SuppressionEvent(PrivateMessageReceivedEvent event, EditEvent ee) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(ee.etape == 1){ //Enregistrement de l'instance à supprimer et Demande confirmation
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.setOd(getOrganizedDateByUser(user,val-1));

                MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Suppression d'un event");
                eb.setTitle("Confirmer votre choix a l'aide du numero: ");
                eb.setDescription("1 :white_check_mark: Confirmé\n2 :x: Annulé");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation de la suppression");
                listEventEdited.remove(ee);
            }
            catch (EmptyArrayException ex){
                MessageUtils.SendPrivateMessage(user,":x: Aucun event n'a pu être trouvé, annulation de la suppression");
                listEventEdited.remove(ee);
            }
            catch (NotFoundException ex){
                MessageUtils.SendPrivateMessage(user,":x: L'event selectionné n'existe pas, annulation de la suppression");
                listEventEdited.remove(ee);
            }
        }
        else if(ee.etape == 2) {
            try{
                int val = Integer.parseInt(msg.getContentDisplay());

                if (val != 1 && val != 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est ni 1 ni 2, annulation de la suppression");
                    listEventEdited.remove(ee);
                    return;
                }

                if (val == 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Suppression Annulé");
                    listEventEdited.remove(ee);
                    return;
                }

                ee.getOd().Delete();

                MessageUtils.SendPrivateMessage(user,":white_check_mark: Evenement supprimé/annulé !");
                MessageUtils.SendPublicMessage(event.getJDA(), ":white_check_mark: Evenement supprimé/annulé : " + ee.getOd().toString());
                listEventEdited.remove(ee);

            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation de la suppression");
                listEventEdited.remove(ee);
            }
        }
    }

    private static void AfficheListRaid(User user){
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

    private static void AfficheListDonjon(User user){
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

    private static void AfficheListJCJ(User user){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Creation d'un event jcj");
        eb.setTitle("Choisissez l'event à l'aide du numero: ");
        String temp = "";
        for (int i = 0; i < LFGdataManagement.JcJ.size(); i++){
            temp = temp.concat((i+1) + " " + LFGdataManagement.JcJ.get(i) + "\n");
        }
        eb.setDescription(temp);
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }

    private static void AfficheListEventCreateByUser(User user) throws EmptyArrayException {
        ArrayList<OrganizedDate> listEvent = LFGdataManagement.getEventsCreateByUser(user);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Suppression d'un event");
        eb.setTitle("Choisissez l'event a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < listEvent.size();i++) {
            //temp = temp.concat((i+1) + " " + listEvent.get(i).toStringWithoutAuthor() + "\n");
            temp = temp.concat(EmoteNumber.get(i+1) + " " + listEvent.get(i).toStringWithoutAuthor() + "\n");
        }
        eb.setDescription(temp);
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }
}
