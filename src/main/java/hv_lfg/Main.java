package hv_lfg;

import hv_lfg.library.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.ArrayList;

public class Main {

    static ArrayList<OrganizedDate> listDate = new ArrayList<>();
    static ArrayList<OrganizedDate> waitListDate = new ArrayList<>();

    public final static ArrayList<Instance> Raid = new ArrayList<>();
    public final static ArrayList<Instance> Donjon = new ArrayList<>();
    public final static ArrayList<Instance> BattleGround = new ArrayList<>();
    public final static ArrayList<Instance> Arene = new ArrayList<>();
    public final static ArrayList<Difficulty> diff = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        InitialiseRaid();
        InitialiseDonjon();
        InitialiseBattleground();
        InitialiseArene();
        InitialiseDifficulty();

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken("NTUwNjkyOTI0NzE1OTU4Mjgz.XVMwhw.W62qdD2FPvgB7rewV5AmpwfknsY");

        builder.addEventListeners(new CreateEventListener());
        builder.addEventListeners(new RegistrationListener());

        builder.build();
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

    private static void InitialiseDifficulty(){
        ResultSet rs = bdd.getTable("SELECT * FROM Difficulty");
        try{
            while (rs.next()){
                Difficulty difficulty = new Difficulty();
                difficulty.setId(rs.getInt("id"));
                difficulty.setName(rs.getString("name"));

                diff.add(difficulty);
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
}
