package be.isservers.hmb.lfg;

import be.isservers.hmb.lfg.library.EmptyArrayException;
import be.isservers.hmb.lfg.library.Instance;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import be.isservers.hmb.utils.MessageUtils;
import be.isservers.hmb.utils.SQLiteSource;
import net.dv8tion.jda.api.entities.Guild;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LFGdataManagement {
    private static final Logger LOGGER = LoggerFactory.getLogger(LFGdataManagement.class);

    public static List<OrganizedDate> listDate = new CopyOnWriteArrayList<>();
    public static List<OrganizedDate> listDateArchived = new CopyOnWriteArrayList<>();

    final static List<Instance> Raid = new CopyOnWriteArrayList<>();
    final static List<Instance> Donjon = new CopyOnWriteArrayList<>();
    final static List<Instance> JcJ = new CopyOnWriteArrayList<>();

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

    public static void InitializeOrganizedDate(ReadyEvent event){
        InitializeGenericOrganizedDate(event,listDate,0);

        for (OrganizedDate od : listDate){
            MessageUtils.SendPublicRichEmbed(event.getJDA(),od);
        }
    }

    public static void InitializeOrdanizedDateArchived(ReadyEvent event){
        InitializeGenericOrganizedDate(event,listDateArchived,1);
    }

    private static void InitializeGenericOrganizedDate(ReadyEvent event,List<OrganizedDate> tmpList,int value) {
        try{
            ResultSet rs = SQLiteSource.getTable("SELECT id,admin,instance,difficulty,date,description,locked FROM LFG_OrganizedDate WHERE archived = "+value+";");
            while (rs.next()){
                try{
                    OrganizedDate od = new OrganizedDate();
                    od.setId(rs.getInt("id"));
                    od.setAdmin(rs.getString("admin"));
                    od.setInstance(getInstanceObjectWithId(rs.getInt("instance")));
                    od.setDifficulty(rs.getInt("difficulty"));
                    od.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(rs.getString("date")));
                    od.setDescription(rs.getString("description"));
                    od.setLock(rs.getInt("locked"));

                    LOGGER.info("Event find: " + od, event.getJDA().getSelfUser().getAsTag());

                    tmpList.add(od);
                }
                catch (NotFoundException ex) { ex.printStackTrace();}

            }
            rs.close();

            ResultSet rsTank = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperTANK;");
            while (rsTank.next()){
                int idEvent = rsTank.getInt("idEvent");
                String idMember = rsTank.getString("idMember");

                for (OrganizedDate od : tmpList){
                    if(od.getId() == idEvent) od.addTankToList(idMember);
                }
            }
            rsTank.close();

            ResultSet rsHeal = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperHEAL;");
            while (rsHeal.next()){
                int idEvent = rsHeal.getInt("idEvent");
                String idMember = rsHeal.getString("idMember");

                for (OrganizedDate od : tmpList){
                    if(od.getId() == idEvent) od.addHealToList(idMember);
                }
            }
            rsHeal.close();

            ResultSet rsDps = SQLiteSource.getTable("SELECT * FROM LFG_ParticiperDPS;");
            while (rsDps.next()){
                int idEvent = rsDps.getInt("idEvent");
                String idMember = rsDps.getString("idMember");

                for (OrganizedDate od : tmpList){
                    if(od.getId() == idEvent) od.addDpsToList(idMember);
                }
            }
            rsDps.close();

            LFGdataManagement.TriListInstance(tmpList);
        }
        catch (SQLException | ParseException ex) { ex.printStackTrace(); }
    }

    static void addListeEvent(OrganizedDate od){
        listDate.add(od);
        SQLiteSource.addEvent(od);
    }

    private static void TriListInstance(List<OrganizedDate> list){
        Collections.sort(list);
    }

    @SuppressWarnings("ConstantConditions")
    public static String getNameOfMember(String id){
        Guild guild = LFGdataManagement.heavenDiscord;

        try{
            return guild.getMemberById(id).getEffectiveName();
        }
        catch (NullPointerException ex){
            return "no data found";
        }
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

    static ArrayList<OrganizedDate> getEventsCreateByUserUnlock(User author) throws EmptyArrayException {
        ArrayList<OrganizedDate> listEvent = new ArrayList<>();

        for (OrganizedDate od : LFGdataManagement.listDate){
            if (od.getAdminId().equals(author.getId()) && od.isActive() && !od.isLocked()) {
                listEvent.add(od);
            }
        }

        if(listEvent.size() == 0)
            throw new EmptyArrayException();

        return listEvent;
    }

    static ArrayList<OrganizedDate> getEventsCreateByUserLock(User author) throws EmptyArrayException {
        ArrayList<OrganizedDate> listEvent = new ArrayList<>();

        for (OrganizedDate od : LFGdataManagement.listDate){
            if (od.getAdminId().equals(author.getId()) && od.isActive() && od.isLocked()) {
                listEvent.add(od);
            }
        }

        if(listEvent.size() == 0)
            throw new EmptyArrayException();

        return listEvent;
    }

    static ArrayList<OrganizedDate> getEventsCreateByUserOnlyActive(User author) throws EmptyArrayException {
        ArrayList<OrganizedDate> listEvent = new ArrayList<>();

        for (OrganizedDate od : LFGdataManagement.listDate){
            if (od.getAdminId().equals(author.getId()) && od.isActive()) {
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

    static OrganizedDate getOrganizedDateByUserLock(User author, int number) throws EmptyArrayException, NotFoundException {
        ArrayList<OrganizedDate> listEvent = getEventsCreateByUser(author);

        if (!(number >= listEvent.size()) && !(number < 0)) {
            OrganizedDate od = listEvent.get(number);
            if (od != null && od.isActive() && od.isLocked()) return od;
        }

        throw new NotFoundException();
    }

    static OrganizedDate getOrganizedDateByUserUnlock(User author, int number) throws EmptyArrayException, NotFoundException {
        ArrayList<OrganizedDate> listEvent = getEventsCreateByUser(author);

        if (!(number >= listEvent.size()) && !(number < 0)) {
            OrganizedDate od = listEvent.get(number);
            if (od != null && od.isActive() && !od.isLocked()) return od;
        }

        throw new NotFoundException();
    }

    static OrganizedDate getOrganizedDateByUserOnlyActive(User author, int number) throws EmptyArrayException, NotFoundException {
        ArrayList<OrganizedDate> listEvent = getEventsCreateByUser(author);

        if (!(number >= listEvent.size()) && !(number < 0)) {
            OrganizedDate od = listEvent.get(number);
            if (od != null && od.isActive()) return od;
        }
        throw new NotFoundException();
    }

    public static void RemoveEvent(OrganizedDate od) {
        listDate.remove(od);
    }


}
