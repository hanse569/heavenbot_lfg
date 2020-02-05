package hv_lfg.library;

import hv_lfg.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class OrganizedDate {
    private String idMessageDiscord;
    private RegisteredMember admin;
    private int instance;
    private int difficulty;
    private int keyNumber = 0;
    private Date date;
    private String description;
    private ArrayList<RegisteredMember> memberlist;

    public int etape = 0;
    public int type = -1;

    public OrganizedDate() { }
    public OrganizedDate(RegisteredMember admin) { this.admin = admin; }
    public void setAdmin(RegisteredMember admin) { this.admin = admin; }
    public void setIdMessageDiscord(String id) { this.idMessageDiscord = id; }
    public void setInstance(int instance) {
        this.instance = instance;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setKeyNumber(int keyNumber) {
        this.keyNumber = keyNumber;
    }

    public RegisteredMember getAdmin() { return admin; }
    public String getIdMessageDiscord() { return idMessageDiscord; }
    public int getInstance() { return instance; }
    public int getDifficulty() { return difficulty; }
    public int getKeyNumber() { return keyNumber; }
    public Date getDateToDate() { return date; }
    public String getDateToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return dateFormat.format(this.getDateToDate());
    }
    public String getDescription() { return description; }
    public ArrayList<RegisteredMember> getMemberlist() { return memberlist; }

    public EmbedBuilder getEmbedBuilder(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(Main.Raid.get(this.getInstance()).getName());
        eb.setDescription(this.getDescription());
        eb.setThumbnail(Main.Raid.get(this.getInstance()).getThumbmail());
        eb.setFooter("Cree par " + this.getAdmin().getName() + " - Powered by HeavenBot");

        eb.addField("Date: ","  " + this.getDateToString(),false);
        eb.addField("TANK","  /",true);
        eb.addField("HEAL","  /",true);
        eb.addField("DPS","  /",false);

        return eb;
    }

    public void addMember(RegisteredMember member){
        memberlist.add(member);
    }
    public void removeMember(RegisteredMember member){
        memberlist.remove(member);
    }

    @Override
    public String toString() {
        return Main.Raid.get(this.getInstance()).getName() + " de " + this.getAdmin().getName() + " le " + this.getDateToString();
    }
}
