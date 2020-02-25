package hv_lfg.library;

import hv_lfg.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.text.DateFormat;
import java.util.*;

public class OrganizedDate {
    public static JDA jda;

    private int id;
    private String idMessageDiscord;
    private RegisteredMember admin;
    private int instance;
    private int difficulty;
    private int keyNumber = 0;
    private Date date;
    private String description;
    private ArrayList<String> TankList = new ArrayList<>();
    private ArrayList<String> HealList = new ArrayList<>();
    private ArrayList<String> DpsList = new ArrayList<>();

    public int etape = 0;
    public int type = -1;

    public OrganizedDate() { }
    public OrganizedDate(RegisteredMember admin) { this.admin = admin; }
    public void setId(int id) { this.id = id; }
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

    public int getId() { return id; }
    public RegisteredMember getAdmin() { return admin; }
    public String getIdMessageDiscord() { return idMessageDiscord; }
    public int getInstance() { return instance; }
    public int getDifficulty() { return difficulty; }
    public int getKeyNumber() { return keyNumber; }
    public Date getDateToDate() { return date; }
    public String getDateToString() {
        /*SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        return dateFormat.format(this.getDateToDate());*/

        return DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT,Locale.FRANCE).format(this.getDateToDate());//Depuis Dossier LCR
    }
    public String getDescription() { return description; }

    public EmbedBuilder getEmbedBuilder(){
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(Main.Raid.get(this.getInstance()).getName());
        eb.setDescription(this.getDescription());
        eb.setThumbnail(Main.Raid.get(this.getInstance()).getThumbmail());
        eb.setFooter("Cree par " + this.getAdmin().getName() + " - Powered by HeavenBot");

        eb.addField("Date: ","  " + this.getDateToString(),false);
        eb.addField("TANK",getStringOfTankList(),true);
        eb.addField("HEAL",getStringOfHealList(),true);
        eb.addField("DPS",getStringOfDpsList(),false);

        return eb;
    }

    public void addTank(String id) {
        removeRoleList(id);

        TankList.add(id);
        bdd.insertOrRemoveRole("INSERT INTO ParticiperTANK VALUES(?,?);",this.getId(),id);
    }
    public void addHeal(String id) {
        removeRoleList(id);

        HealList.add(id);
        bdd.insertOrRemoveRole("INSERT INTO ParticiperHEAL VALUES(?,?);",this.getId(),id);
    }
    public void addDps(String id) {
        removeRoleList(id);

        DpsList.add(id);
        bdd.insertOrRemoveRole("INSERT INTO ParticiperDPS VALUES(?,?);",this.getId(),id);
    }
    public void removeRoleList(String id){
        if(TankList.remove(id)) bdd.insertOrRemoveRole("DELETE FROM ParticiperTANK WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
        if(HealList.remove(id)) bdd.insertOrRemoveRole("DELETE FROM ParticiperHEAL WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
        if(DpsList.remove(id))  bdd.insertOrRemoveRole("DELETE FROM ParticiperDPS WHERE idEvent = ? AND idMember = ?;",this.getId(),id);
    }

    private String getStringOfTankList(){
        if(TankList.size() > 0){
            String buffer = "";
            for (String tmp : TankList){
                buffer = buffer.concat(Objects.requireNonNull(jda.getGuildById("241110646677176320")).getMemberById(tmp).getEffectiveName() + "  ");
            }
            return buffer;
        }
        return " / ";
    }
    private String getStringOfHealList(){
        if(HealList.size() > 0){
            String buffer = "";
            for (String tmp : HealList){
                buffer = buffer.concat(Objects.requireNonNull(jda.getGuildById("241110646677176320")).getMemberById(tmp).getEffectiveName() + "  ");
            }
            return buffer;
        }
        return " / ";
    }
    private String getStringOfDpsList(){
        if(DpsList.size() > 0){
            String buffer = "";
            for (String tmp : DpsList){
                buffer = buffer.concat(Objects.requireNonNull(jda.getGuildById("241110646677176320")).getMemberById(tmp).getEffectiveName() + "  ");
            }
            return buffer;
        }
        return " / ";
    }

    @Override
    public String toString() {
        return Main.Raid.get(this.getInstance()).getName() + " de " + this.getAdmin().getName() + " le " + this.getDateToString();
    }
}
