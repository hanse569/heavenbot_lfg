package hv_lfg;

import hv_lfg.library.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Main {

    static ArrayList<OrganizedDate> listDate = new ArrayList<>();
    static ArrayList<OrganizedDate> waitListDate = new ArrayList<>();

    final static ArrayList<Instance> Raid = new ArrayList<>();
    final static ArrayList<Instance> Donjon = new ArrayList<>();
    private final static ArrayList<Instance> BattleGround = new ArrayList<>();
    private final static ArrayList<Instance> Arene = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        InitialiseRaid();
        InitialiseDonjon();
        InitialiseBattleground();
        InitialiseArene();

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(Settings.getDiscordToken());

        builder.addEventListeners(new CreateEventListener());
        builder.addEventListeners(new RegistrationListener());

        builder.addEventListeners(new BotMusic());

        builder.setActivity(Activity.watching("Comment faire fonctionner ce bot de merde !"));

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

    public static String getNameOfMember(JDA jda, String id){
        return Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById(Settings.getIdDiscordHeaven())).getMemberById(id)).getEffectiveName();
    }
}
