package be.isservers.hmb.lfg;

import be.isservers.hmb.lfg.library.*;
import be.isservers.hmb.utils.SQLiteSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class LFGdataManagement {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGdataManagement.class);

    static ArrayList<OrganizedDate> listDate = new ArrayList<>();
    static ArrayList<OrganizedDate> waitListDate = new ArrayList<>();

    final static ArrayList<Instance> Raid = new ArrayList<>();
    final static ArrayList<Instance> Donjon = new ArrayList<>();
    final static ArrayList<Instance> JcJ = new ArrayList<>();

    public static Guild heavenDiscord;

    static  {
        InitialiseRaid();
        InitialiseDonjon();
        InitialiseJcJ();
    }

    private static void InitialiseRaid(){
        ResultSet rs = SQLiteSource.getTable("SELECT * FROM ViewRaid");
        try{
            while (rs.next()){
                Instance instance = new Instance();
                instance.setIdInstance(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setType(1);
                instance.setThumbmail(rs.getString("thumbmail"));
                Raid.add(instance);
            }
            rs.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    private static void InitialiseDonjon(){
        ResultSet rs = SQLiteSource.getTable("SELECT * FROM ViewDonjon");
        try{
            while (rs.next()){
                Instance instance = new Instance();
                instance.setIdInstance(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setType(2);
                instance.setThumbmail(rs.getString("thumbmail"));
                Donjon.add(instance);
            }
            rs.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    private static void InitialiseJcJ(){
        ResultSet rs = SQLiteSource.getTable("SELECT * FROM ViewPVP");
        try{
            while (rs.next()){
                Instance instance = new Instance();
                instance.setIdInstance(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setType(3);
                instance.setThumbmail(rs.getString("thumbmail"));
                JcJ.add(instance);
            }
            rs.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void InitializeOrganizedDate(ReadyEvent event) {
        try{
            ResultSet rs = SQLiteSource.getTable("SELECT id,idMessageDiscord,admin,instance,difficulty,date,description FROM LFG_OrganizedDate;");
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

                    LFGdataManagement.listDate.add(od);
                    LFGmain.nr++;
                }
                catch (NotFoundException ex) { ex.printStackTrace();}

            }
            rs.close();

            ResultSet rsTank = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperTANK;");
            while (rsTank.next()){
                int idEvent = rsTank.getInt("idEvent");
                String idMember = rsTank.getString("idMember");

                for (OrganizedDate od : LFGdataManagement.listDate){
                    if(od.getId() == idEvent) od.addTankToList(idMember);
                }
            }
            rsTank.close();

            ResultSet rsHeal = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperHEAL;");
            while (rsHeal.next()){
                int idEvent = rsHeal.getInt("idEvent");
                String idMember = rsHeal.getString("idMember");

                for (OrganizedDate od : LFGdataManagement.listDate){
                    if(od.getId() == idEvent) od.addHealToList(idMember);
                }
            }
            rsHeal.close();

            ResultSet rsDps = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperDPS;");
            while (rsDps.next()){
                int idEvent = rsDps.getInt("idEvent");
                String idMember = rsDps.getString("idMember");

                for (OrganizedDate od : LFGdataManagement.listDate){
                    if(od.getId() == idEvent) od.addDpsToList(idMember);
                }
            }
            rsDps.close();

            LFGdataManagement.TriListInstance();

            for (OrganizedDate od : LFGdataManagement.listDate){

                if(od.getInstance().getType() == 1 || od.getInstance().getType() == 2){
                    MessageUtils.SendPublicRichEmbedPVE(event.getJDA(),od);
                }
                else if(od.getInstance().getType() == 3) {
                    MessageUtils.SendPublicRichEmbedPVP(event.getJDA(),od);
                }
            }

        }
        catch (SQLException | ParseException ex) { ex.printStackTrace(); }
    }

    static void addListeEvent(OrganizedDate od){
        waitListDate.add(od);
    }

    static void confirmEvent(OrganizedDate od,String id){
        waitListDate.remove(od);

        od.setIdMessageDiscord(id);
        listDate.add(od);

        SQLiteSource.addEvent(od);
    }

    private static void TriListInstance(){
        Collections.sort(listDate);
    }

    public static String getNameOfMember(String id){
        Guild guild = LFGdataManagement.heavenDiscord;
        Member member = guild.retrieveMemberById(id).complete();
        return member.getEffectiveName();
    }

    private static Instance getInstanceObjectWithId(int val) throws NotFoundException {
        for (Instance obj : LFGdataManagement.Raid){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        for (Instance obj : LFGdataManagement.Donjon){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        for (Instance obj : LFGdataManagement.JcJ){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        throw new NotFoundException();
    }

    static Instance getInstanceObjectWithOrder(int type, int val) throws NotFoundException{
        Instance instance;

        if(type == 1){
            instance = LFGdataManagement.Raid.get(val);
            if(instance != null) return instance;
        }
        else if(type == 2){
            instance = LFGdataManagement.Donjon.get(val);
            if(instance != null) return instance;
        }
        else if (type == 3){
            instance = LFGdataManagement.JcJ.get(val);
            if(instance != null) return instance;
        }

        throw new NotFoundException();
    }

    static ArrayList<OrganizedDate> getEventsCreateByUser(User author) throws EmptyArrayException {
        ArrayList<OrganizedDate> listEvent = new ArrayList<>();

        for (OrganizedDate od : LFGdataManagement.listDate){
            if (od.getAdminId().equals(author.getId())) {
                listEvent.add(od);
            }
        }

        if(listEvent.size() == 0)
            throw new EmptyArrayException();

        return listEvent;
    }

    static OrganizedDate getOrganizedDateByUser(User author, int number) throws EmptyArrayException, NotFoundException {
        ArrayList<OrganizedDate> listEvent = getEventsCreateByUser(author);

        OrganizedDate od = listEvent.get(number);
        if (od != null) return od;

        throw new NotFoundException();
    }

    public static boolean RemoveEvent(OrganizedDate od) {
        return listDate.remove(od);
    }


}
