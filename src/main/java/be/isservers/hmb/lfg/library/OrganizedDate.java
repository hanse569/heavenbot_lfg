package be.isservers.hmb.lfg.library;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.SQLiteSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

@SuppressWarnings({"ConstantConditions"})
public class OrganizedDate implements Comparable<OrganizedDate>{

    private int id;
    private String idMessageDiscord = null;
    private String admin;
    private Instance instance;
    private int difficulty;
    private Date date;
    private String description;
    private boolean isLock = false;
    private final ArrayList<String> TankList = new ArrayList<>();
    private final ArrayList<String> HealList = new ArrayList<>();
    private final ArrayList<String> DpsList = new ArrayList<>();

    public OrganizedDate() { }
    public OrganizedDate(String admin) { this.setAdmin(admin); }
    public void setId(int id) { this.id = id; }
    public void setAdmin(String admin) { this.admin = admin; }
    public void setIdMessageDiscord(String id) {
        this.idMessageDiscord = id;
        if(!this.getDateToDate().before(Calendar.getInstance().getTime()) && !this.isLocked()){
            this.RefreshEvent();
        }
    }
    public void setInstance(Instance instance) {
        this.instance = instance;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setLock(int lock) { this.setLock(lock!=0); }
    public void setLock(boolean lock) { this.isLock = lock; }

    public int getId() { return id; }
    public String getAdminId() { return this.admin; }
    public String getAdmin() { return LFGdataManagement.getNameOfMember(this.admin); }
    public String getIdMessageDiscord() { return idMessageDiscord; }
    public Instance getInstance() { return instance; }
    public int getDifficulty() { return difficulty; }
    public boolean isLocked() { return isLock; }
    public Date getDateToDate() { return date; }
    public String getDateToString() {
        return DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT,Locale.FRANCE).format(this.getDateToDate());//Depuis Dossier LCR
    }
    public String getDateToRequest(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return dateFormat.format(this.getDateToDate());
    }
    public String getDescription() { return description; }

    public EmbedBuilder getEmbedBuilder(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(this.getInstance().getName());
        eb.setDescription(this.getDescription());
        eb.setImage(this.getInstance().getThumbmail());
        eb.setFooter("Cree par " + this.getAdmin());


        if (this.getDateToDate().before(Calendar.getInstance().getTime())) {
            eb.setColor(Color.DARK_GRAY);
        }
        else if (this.isLocked()){
            eb.setColor(Color.ORANGE);
        }
        else {
            eb.setColor(Color.GREEN);
        }

        eb.addField("Date: ","  " + this.getDateToString(),false);

        if(this.getInstance().getType() == 1 || this.getInstance().getType() == 2){
            eb.addField("TANK",getStringOfList(TankList),true);
            eb.addField("HEAL",getStringOfList(HealList),true);
            eb.addField("DPS",getStringOfList(DpsList),false);
        }
        else if(this.getInstance().getType() == 3) {
            eb.addField("PARTICIPANTS",getStringOfList(DpsList),true);
        }

        return eb;
    }

    public void addTankToList(String id){
        TankList.add(id);
    }
    public void addTank(String id) {
        removeRoleList(id);
        TankList.add(id);
        SQLiteSource.insertOrRemoveRole("INSERT INTO LFG_ParticiperTANK VALUES(?,?);",this.getId(),id);
    }

    public void addHealToList(String id){
        HealList.add(id);
    }
    public void addHeal(String id) {
        removeRoleList(id);
        HealList.add(id);
        SQLiteSource.insertOrRemoveRole("INSERT INTO LFG_ParticiperHEAL VALUES(?,?);",this.getId(),id);
    }

    public void addDpsToList(String id){
        DpsList.add(id);
    }
    public void addDps(String id) {
        removeRoleList(id);
        DpsList.add(id);
        SQLiteSource.insertOrRemoveRole("INSERT INTO LFG_ParticiperDPS VALUES(?,?);",this.getId(),id);
    }
    public void removeRoleList(String id){
        if(TankList.remove(id)) SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperTANK WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
        if(HealList.remove(id)) SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperHEAL WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
        if(DpsList.remove(id))  SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperDPS WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
    }

    public void Delete() {
        for (String user : TankList) {
            SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperTANK WHERE idEvent = ? AND idMember = ?;",this.getId(),user);
        }

        for (String user : HealList) {
            SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperTANK WHERE idEvent = ? AND idMember = ?;",this.getId(),user);
        }

        for (String user : DpsList) {
            SQLiteSource.insertOrRemoveRole("DELETE FROM LFG_ParticiperTANK WHERE idEvent = ? AND idMember = ?;",this.getId(),user);
        }

        SQLiteSource.removeEvent("DELETE FROM LFG_OrganizedDate WHERE id = ?;",this.getId());
        LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelDonjon()).deleteMessageById(getIdMessageDiscord()).queue();
        LFGdataManagement.RemoveEvent(this);
    }

    public void RefreshEvent() {
        LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelDonjon()).editMessageById(this.getIdMessageDiscord(),this.getEmbedBuilder().build()).queue();

        TextChannel tc = LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelDonjon());
        if(this.getDateToDate().before(Calendar.getInstance().getTime()) || this.isLocked()){
            tc.removeReactionById(this.getIdMessageDiscord(),Config.getEmojiTANK()).queue();
            tc.removeReactionById(this.getIdMessageDiscord(),Config.getEmojiHEAL()).queue();
            tc.removeReactionById(this.getIdMessageDiscord(),Config.getEmojiDPS()).queue();
            tc.removeReactionById(this.getIdMessageDiscord(),Config.getEmojiDELETE()).queue();
        }
        else {
            if(this.getInstance().getType() == 1 || this.getInstance().getType() == 2){
                tc.addReactionById(this.getIdMessageDiscord(),Config.getEmojiTANK()).queue();
                tc.addReactionById(this.getIdMessageDiscord(),Config.getEmojiHEAL()).queue();
            }
            tc.addReactionById(this.getIdMessageDiscord(),Config.getEmojiDPS()).queue();
            tc.addReactionById(this.getIdMessageDiscord(),Config.getEmojiDELETE()).queue();
        }
    }

    public boolean isActive() {
        return (!this.getDateToDate().before(Calendar.getInstance().getTime()));
    }

    private String getStringOfList(List<String> list) {
        if(list.size() > 0){
            String buffer = "";
            for (String tmp : list){
                buffer = buffer.concat(LFGdataManagement.getNameOfMember(tmp) + "  ");
            }
            return buffer;
        }
        return " / ";
    }

    @Override
    public String toString() {
        return this.getDateToRequest() + " " + this.getInstance().getName() + " de " + this.getAdmin();
    }

    public String toStringWithoutAuthor() {
        return this.getInstance().getName() + " " + this.getDateToRequest();
    }

    @Override
    public int compareTo(@NotNull OrganizedDate o) {
        return this.date.compareTo(o.date);
    }


}
