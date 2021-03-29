package be.isservers.hmb.lfg;

import be.isservers.hmb.lfg.library.EmptyArrayException;
import be.isservers.hmb.lfg.library.Instance;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import be.isservers.hmb.utils.EmoteNumber;
import be.isservers.hmb.utils.MessageUtils;
import be.isservers.hmb.utils.SQLiteSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static be.isservers.hmb.lfg.LFGdataManagement.*;

public class LFGmain extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGmain.class);
    private static final ArrayList<EditEvent> listEventEdited = new ArrayList<>();

    public static void Clear(TextChannel channel){
        List<Message> messages = channel.getHistory().retrievePast(50).complete();
        if(!(messages.isEmpty() || messages.size() < 2)) {
            LOGGER.info("Deleting messages in the channel " + channel);
            channel.deleteMessages(messages).complete();
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
                    AfficheList(ctx.getAuthor(), Raid,"Creation d'un raid");

                    ee.type = 1;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("donjon")){
                    ee = new EditEvent(EditEvent.ADD,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    AfficheList(ctx.getAuthor(), Donjon,"Creation d'un donjon");

                    ee.type = 2;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("jcj")){
                    ee = new EditEvent(EditEvent.ADD,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    AfficheList(ctx.getAuthor(), JcJ,"Creation d'un event jcj");

                    ee.type = 3;
                    ee.etape++;
                }
                else if(args.get(0).startsWith("delete")){
                    ee = new EditEvent(EditEvent.DELETE,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    try {
                        AfficheListEventCreateByUser(ctx.getAuthor(),LFGdataManagement.getEventsCreateByUser(ctx.getAuthor()),"Suppression d'un event");
                        ee.etape++;
                    }
                    catch (EmptyArrayException ex) {
                        MessageUtils.SendPrivateMessage(ctx.getAuthor(),":x: Aucun event n'a pu être trouvé, annulation de la suppression");
                        listEventEdited.remove(ee);
                    }
                }
                else if(args.get(0).startsWith("lock")){
                    ee = new EditEvent(EditEvent.LOCKED,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    try {
                        AfficheListEventCreateByUser(ctx.getAuthor(),LFGdataManagement.getEventsCreateByUserUnlock(ctx.getAuthor()),"Verrouillage d'un event");
                        ee.etape++;
                    }
                    catch (EmptyArrayException ex) {
                        MessageUtils.SendPrivateMessage(ctx.getAuthor(),":x: Aucun event n'a pu être trouvé");
                        listEventEdited.remove(ee);
                    }
                }
                else if(args.get(0).startsWith("unlock")){
                    ee = new EditEvent(EditEvent.UNLOCKED,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    try {
                        AfficheListEventCreateByUser(ctx.getAuthor(),LFGdataManagement.getEventsCreateByUserLock(ctx.getAuthor()),"Deverrouillage d'un event");
                        ee.etape++;
                    }
                    catch (EmptyArrayException ex) {
                        MessageUtils.SendPrivateMessage(ctx.getAuthor(),":x: Aucun event n'a pu être trouvé");
                        listEventEdited.remove(ee);
                    }
                }
                else if(args.get(0).startsWith("edit")){
                    ee = new EditEvent(EditEvent.MODIFY,new OrganizedDate(ctx.getAuthor().getId()));
                    listEventEdited.add(ee);
                    try {
                        AfficheListEventCreateByUser(ctx.getAuthor(),LFGdataManagement.getEventsCreateByUserOnlyActive(ctx.getAuthor()),"Edtion d'un event");
                        ee.etape++;
                    }
                    catch (EmptyArrayException ex) {
                        MessageUtils.SendPrivateMessage(ctx.getAuthor(),":x: Aucun event n'a pu être trouvé");
                        listEventEdited.remove(ee);
                    }
                }
                else {
                    AfficheHelp(ctx.getAuthor());
                }
            }
            else{
                AfficheHelp(ctx.getAuthor());
            }
        }
        else
        {
            if (args.size() > 0 && args.get(0).startsWith("cancel")) {
                MessageUtils.SendPrivateMessage(ctx.getAuthor(),":white_check_mark: annulation de l'action en cours");
                listEventEdited.remove(ee);
            }
            else if (ee.getAction() == EditEvent.ADD){
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
            else if(ee.getAction() == EditEvent.LOCKED){
                VerrouillageEvent(ctx,ee);
            }
            else if(ee.getAction() == EditEvent.UNLOCKED){
                DeverrouillageEvent(ctx,ee);
            }
            else if(ee.getAction() == EditEvent.MODIFY){
                EditionEvent(ctx,ee);
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException | NotFoundException | IndexOutOfBoundsException ex){
                if (ee.type == 1)
                    AfficheList(user, Raid,"Creation d'un raid");
                else if(ee.type == 2)
                    AfficheList(user, Donjon,"Creation d'un donjon");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");

                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(ee.etape == 5){
            ee.getOd().setDescription(msg.getContentDisplay());

            MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
            ee.etape++;

            listEventEdited.remove(ee);
            LFGdataManagement.addListeEvent(ee.getOd());

            MessageUtils.SendPublicRichEmbed(event.getJDA(),ee.getOd());

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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException | NotFoundException ex){
                AfficheList(user, JcJ,"Creation d'un event jcj");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un raid");
                eb.setTitle("Choisissez la difficulte a l'aide du numero: ");
                eb.setDescription("1 Normal\n2 Heroique\n3 Mythique\n4 Marcheur du temps");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Creation d'un event jcj");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        } //Enregistrement heure et demande descritpion
        else if(ee.etape == 5){
            ee.getOd().setDescription(msg.getContentDisplay());

            MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
            ee.etape++;

            listEventEdited.remove(ee);
            LFGdataManagement.addListeEvent(ee.getOd());

            MessageUtils.SendPublicRichEmbed(event.getJDA(),ee.getOd());

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
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
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

    private static void VerrouillageEvent(PrivateMessageReceivedEvent event, EditEvent ee) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(ee.etape == 1){ //Enregistrement de l'instance à supprimer et Demande confirmation
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.setOd(getOrganizedDateByUserUnlock(user,val));

                MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Verrouillage d'un event");
                eb.setTitle("Confirmer votre choix a l'aide du numero: ");
                eb.setDescription("1 :white_check_mark: Confirmé\n2 :x: Annulé");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation du verrouillage");
                listEventEdited.remove(ee);
            }
            catch (EmptyArrayException ex){
                MessageUtils.SendPrivateMessage(user,":x: Aucun event n'a pu être trouvé, annulation du verrouillage");
                listEventEdited.remove(ee);
            }
            catch (NotFoundException ex){
                MessageUtils.SendPrivateMessage(user,":x: L'event selectionné n'existe pas, annulation du verrouillage");
                listEventEdited.remove(ee);
            }
        }
        else if(ee.etape == 2) {
            try{
                int val = Integer.parseInt(msg.getContentDisplay());

                if (val != 1 && val != 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est ni 1 ni 2, annulation du verrouillage");
                    listEventEdited.remove(ee);
                    return;
                }

                if (val == 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Verrouillage Annulé");
                    listEventEdited.remove(ee);
                    return;
                }

                OrganizedDate od = ee.getOd();

                od.setLock(!od.isLocked());
                SQLiteSource.updateValueOfEvent("locked",od.isLocked()?1:0,od.getId());
                od.RefreshEvent();

                MessageUtils.SendPrivateMessage(user,":white_check_mark: Verrouillage confirmé !");
                listEventEdited.remove(ee);

            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation du Verrouillage");
                listEventEdited.remove(ee);
            }
        }
    }

    private static void DeverrouillageEvent(PrivateMessageReceivedEvent event, EditEvent ee) {
        User user = event.getAuthor();
        Message msg = event.getMessage();

        if(ee.etape == 1){ //Enregistrement de l'instance à supprimer et Demande confirmation
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.setOd(getOrganizedDateByUserLock(user,val));

                MessageUtils.SendPrivateRichEmbed(user,ee.getOd().getEmbedBuilder());
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Devrouillage d'un event");
                eb.setTitle("Confirmer votre choix a l'aide du numero: ");
                eb.setDescription("1 :white_check_mark: Confirmé\n2 :x: Annulé");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);

                ee.etape++;
            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation du deverrouillage");
                listEventEdited.remove(ee);
            }
            catch (EmptyArrayException ex){
                MessageUtils.SendPrivateMessage(user,":x: Aucun event n'a pu être trouvé, annulation du deverrouillage");
                listEventEdited.remove(ee);
            }
            catch (NotFoundException ex){
                MessageUtils.SendPrivateMessage(user,":x: L'event selectionné n'existe pas, annulation du deverrouillage");
                listEventEdited.remove(ee);
            }
        }
        else if(ee.etape == 2) {
            try{
                int val = Integer.parseInt(msg.getContentDisplay());

                if (val != 1 && val != 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est ni 1 ni 2, annulation du deverrouillage");
                    listEventEdited.remove(ee);
                    return;
                }

                if (val == 2) {
                    MessageUtils.SendPrivateMessage(user,":x: Deverrouillage Annulé");
                    listEventEdited.remove(ee);
                    return;
                }

                OrganizedDate od = ee.getOd();

                od.setLock(!od.isLocked());
                SQLiteSource.updateValueOfEvent("locked",od.isLocked()?1:0,od.getId());
                od.RefreshEvent();

                MessageUtils.SendPrivateMessage(user,":white_check_mark: Deverrouillage confirmé !");
                listEventEdited.remove(ee);

            }
            catch(NumberFormatException ex){
                MessageUtils.SendPrivateMessage(user,":x: Valeur encodé n'est pas un nombre, annulation du deverrouillage");
                listEventEdited.remove(ee);
            }
        }
    }

    private static void EditionEvent(PrivateMessageReceivedEvent event, EditEvent ee){
        User user = event.getAuthor();
        Message msg = event.getMessage();


        if(ee.etape == 1){ //Enregistrement de l'instance à supprimer et Demande confirmation
            try{
                int val = Integer.parseInt(msg.getContentDisplay());
                ee.setOd(getOrganizedDateByUserOnlyActive(user,val-1));

                AfficheEditionEventMainMenu(user,ee.getOd().getEmbedBuilder());

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
        else if(ee.etape == 2){
            try {
                int val = Integer.parseInt(msg.getContentDisplay());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Edition d'un event");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                switch (val) {
                    case 1:
                        ee.etape = 3;

                        eb.setTitle("Indique une description: ");
                        MessageUtils.SendPrivateRichEmbed(user,eb);
                        break;

                    case 2:
                        ee.etape = 4;


                        eb.setTitle("Choisissez la date: ");
                        eb.setDescription("*Exemple*: 05-01-2019");
                        MessageUtils.SendPrivateRichEmbed(user,eb);
                        break;

                    case 3:
                        ee.etape = 5;

                        eb.setTitle("Choisissez l'heure: ");
                        eb.setDescription("*Exemple*: 21:00");
                        MessageUtils.SendPrivateRichEmbed(user,eb);
                        break;

                    case 4:
                        MessageUtils.SendPrivateMessage(user,":white_check_mark: Edition terminé !");
                        listEventEdited.remove(ee);
                        break;

                    default:
                        throw new NotFoundException();
                }
            }
            catch(NotFoundException | NumberFormatException ex){
                AfficheEditionEventMainMenu(user,ee.getOd().getEmbedBuilder());
            }
        }
        else if(ee.etape == 3) {
            ee.getOd().setDescription(msg.getContentDisplay());
            SQLiteSource.updateValueOfEvent("description",ee.getOd().getDescription(),ee.getOd().getId());

            MessageUtils.SendPrivateMessage(user,":white_check_mark: Edition validé, retour au menu principal !");
            AfficheEditionEventMainMenu(user,ee.getOd().getEmbedBuilder());

            ee.etape = 2;
        }
        else if(ee.etape == 4) {
            try{
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Calendar oldDate = Calendar.getInstance();
                oldDate.setTime(ee.getOd().getDateToDate());

                Calendar newDate = Calendar.getInstance();
                newDate.setTime(df.parse(msg.getContentDisplay()));
                newDate.set(Calendar.HOUR_OF_DAY,oldDate.get(Calendar.HOUR_OF_DAY));
                newDate.set(Calendar.MINUTE,oldDate.get(Calendar.MINUTE));
                ee.getOd().setDate(newDate.getTime());
                SQLiteSource.updateValueOfEvent("date",ee.getOd().getDateToRequest(),ee.getOd().getId());


                MessageUtils.SendPrivateMessage(user,":white_check_mark: Edition validé, retour au menu principal !");
                AfficheEditionEventMainMenu(user,ee.getOd().getEmbedBuilder());

                ee.etape = 2;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Edition d'un event");
                eb.setTitle("Choisissez la date: ");
                eb.setDescription("*Exemple*: 05-01-2019");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        }
        else if(ee.etape == 5) {
            try{
                DateFormat df = new SimpleDateFormat("HH:mm");
                Calendar newHour = Calendar.getInstance();
                newHour.setTime(df.parse(msg.getContentDisplay()));

                Calendar date = Calendar.getInstance();
                date.setTime(ee.getOd().getDateToDate());
                date.set(Calendar.HOUR_OF_DAY,newHour.get(Calendar.HOUR_OF_DAY));
                date.set(Calendar.MINUTE,newHour.get(Calendar.MINUTE));
                ee.getOd().setDate(date.getTime());
                SQLiteSource.updateValueOfEvent("date",ee.getOd().getDateToRequest(),ee.getOd().getId());

                MessageUtils.SendPrivateMessage(user,":white_check_mark: Edition validé, retour au menu principal !");
                AfficheEditionEventMainMenu(user,ee.getOd().getEmbedBuilder());

                ee.etape = 2;
            } catch (ParseException ex){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Edition d'un event");
                eb.setTitle("Choisissez l'heure: ");
                eb.setDescription("*Exemple*: 21:00");
                eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
                MessageUtils.SendPrivateRichEmbed(user,eb);
            }
        }
    }

    private static void AfficheList(User user, List<Instance> listInstance,String titre) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(titre);
        eb.setTitle("Choisissez l'instance a l'aide du numero: ");
        String temp = "";
        for (int i = 0; i < listInstance.size(); i++){
            temp = temp.concat(EmoteNumber.get(i+1) + " " + listInstance.get(i) + "\n");
        }
        eb.setDescription(temp);
        eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }

    private static void AfficheListEventCreateByUser(User user,List<OrganizedDate> listEvent,String titre) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(titre);
        eb.setTitle("Choisissez l'event a l'aide du numero: ");
        String temp = "";
        for (int i = 0;i < listEvent.size();i++) {
            temp = temp.concat(EmoteNumber.get(i+1) + " " + listEvent.get(i).toStringWithoutAuthor() + "\n");
        }
        eb.setDescription(temp);
        eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }

    private static void AfficheEditionEventMainMenu(User user,EmbedBuilder ebtmp) {
        MessageUtils.SendPrivateRichEmbed(user,ebtmp);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Edition d'un event");
        eb.setTitle("Choisissez l'element à éditer: ");
        StringBuilder sb = new StringBuilder()
                .append(EmoteNumber.get(1)).append(" Description\n")
                .append(EmoteNumber.get(2)).append(" Date\n")
                .append(EmoteNumber.get(3)).append(" Heure\n")
                .append(EmoteNumber.get(4)).append(" Quitter\n");
        eb.setDescription(sb.toString());
        eb.setFooter("Annuler vos action à tout moment avec !lfg cancel");
        MessageUtils.SendPrivateRichEmbed(user,eb);
    }

    private static void AfficheHelp(User user) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Commande disponible");
        eb.addField("!lfg raid","```Permet de planifier un raid de Shadowlands```",false);
        eb.addField("!lfg donjon","```Permet de planifier un donjon de Shadowlands```",false);
        eb.addField("!lfg jcj","```Permet de planifier un évènement PVP```",false);
        eb.addField("!lfg edit","```Permet de modifier un évènement```",false);
        eb.addField("!lfg delete","```Permet de supprimer un évènement```",false);
        eb.addField("!lfg lock","```Permet de verrouiller un évènement que l'on a créé```",false);
        eb.addField("!lfg unlock","```Permet de déverrouiller un évènement que l'on a verrouillé```",false);
        eb.addField("!lfg cancel","```Permet d'annuler toutes actions en cours```",false);
        eb.setColor(Color.decode("#FF7A00"));
        eb.setFooter("Powered by E-Van","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");

        MessageUtils.SendPrivateRichEmbed(user,eb);
    }
}
