package be.isservers.hmb.lfg;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.Instance;
import be.isservers.hmb.lfg.library.NotFoundException;
import be.isservers.hmb.lfg.library.OrganizedDate;
import be.isservers.hmb.lfg.library.bdd;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LFGdata {
    static ArrayList<OrganizedDate> listDate = new ArrayList<>();
    static ArrayList<OrganizedDate> waitListDate = new ArrayList<>();

    final static ArrayList<Instance> Raid = new ArrayList<>();
    final static ArrayList<Instance> Donjon = new ArrayList<>();
    private final static ArrayList<Instance> BattleGround = new ArrayList<>();
    private final static ArrayList<Instance> Arene = new ArrayList<>();

    static Guild heavenDiscord;

    static  {
        InitialiseRaid();
        InitialiseDonjon();
        InitialiseBattleground();
        InitialiseArene();
    }

    private static void InitialiseRaid(){
        ResultSet rs = bdd.getTable("SELECT * FROM ViewRaid");
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
        ResultSet rs = bdd.getTable("SELECT * FROM ViewDonjon");
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

    private static void InitialiseBattleground(){
        ResultSet rs = bdd.getTable("SELECT * FROM ViewBattleground");
        try{
            while (rs.next()){
                Instance instance = new Instance();
                instance.setIdInstance(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setThumbmail(rs.getString("thumbmail"));
                BattleGround.add(instance);
            }
            rs.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    private static void InitialiseArene(){
        ResultSet rs = bdd.getTable("SELECT * FROM ViewArene");
        try{
            while (rs.next()){
                Instance instance = new Instance();
                instance.setIdInstance(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setThumbmail(rs.getString("thumbmail"));
                Arene.add(instance);
            }
            rs.close();
        }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    static void addListeEvent(OrganizedDate od){
        waitListDate.add(od);
    }

    static void confirmEvent(OrganizedDate od,String id){
        waitListDate.remove(od);

        od.setIdMessageDiscord(id);
        listDate.add(od);

        bdd.addEvent(od);
    }

    static void TriListInstance(){
        Collections.sort(listDate);
    }

    public static String getNameOfMember(String id){
        Guild guild = LFGdata.heavenDiscord;
        Member member = guild.retrieveMemberById(id).complete();
        return member.getEffectiveName();
    }

    public static Instance getInstanceObjectWithId(int val) throws NotFoundException {
        for (Instance obj : LFGdata.Raid){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        for (Instance obj : LFGdata.Donjon){
            if(obj.getIdInstance() == val){
                return obj;
            }
        }

        throw new NotFoundException();
    }

    public static Instance getInstanceObjectWithOrder(int type,int val) throws NotFoundException{
        Instance instance;

        if(type == 1){
            instance = LFGdata.Raid.get(val);
            if(instance != null) return instance;
        }
        else if(type == 2){
            instance = LFGdata.Donjon.get(val);
            if(instance != null) return instance;
        }

        throw new NotFoundException();
    }


}
