package be.isservers.hmb;

import be.isservers.hmb.utils.SQLiteSource;

import java.sql.SQLException;
import java.util.HashMap;

public class Config {

    private static HashMap<String,String> data = new HashMap<>();

    public static void Initialize(){
        try{
            data.put("token",SQLiteSource.getTable(getQuery("TOKEN")).getString("value"));
            data.put("prefix",SQLiteSource.getTable(getQuery("PREFIX")).getString("value"));
            data.put("owner_id",SQLiteSource.getTable(getQuery("OWNER_ID")).getString("value"));
            data.put("id_discord_heaven",SQLiteSource.getTable(getQuery("IDDISCORDHEAVEN")).getString("value"));
            data.put("id_channel_evan",SQLiteSource.getTable(getQuery("IDCHANNELEVAN")).getString("value"));
            data.put("id_channel_donjon",SQLiteSource.getTable(getQuery("IDCHANNELDONJON")).getString("value"));
            data.put("id_channel_gazette",SQLiteSource.getTable(getQuery("IDCHANNELGAZETTE")).getString("value"));
            data.put("emoji_tank",SQLiteSource.getTable(getQuery("EMOJITANK")).getString("value"));
            data.put("emoji_heal",SQLiteSource.getTable(getQuery("EMOJIHEAL")).getString("value"));
            data.put("emoji_dps",SQLiteSource.getTable(getQuery("EMOJIDPS")).getString("value"));
            data.put("emoji_delete",SQLiteSource.getTable(getQuery("EMOJIDELETE")).getString("value"));
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private static String getQuery(String value){
        return "SELECT value FROM MB_guild_settings_new WHERE parameter LIKE '"+value+"'";
    }

    public static String getToken(){ return data.get("token"); }
    public static String getPrefix(){ return data.get("prefix"); }
    public static String getOwnerId(){ return data.get("owner_id"); }

    public static String getIdDiscordHeaven(){ return data.get("id_discord_heaven"); }
    public static String getIdChannelEvan(){ return data.get("id_channel_evan"); }
    public static String getIdChannelDonjon(){ return data.get("id_channel_donjon"); }
    public static String getIdChannelGazette(){ return data.get("id_channel_gazette"); }

    public static String getEmojiTANK(){ return data.get("emoji_tank"); }//Pour l'envoie du message dans la fonction SendPublicRichEmbed de CreateEventListener
    public static String getEmojiHEAL(){ return data.get("emoji_heal"); }
    public static String getEmojiDPS(){ return data.get("emoji_dps"); }
    public static String getEmojiDELETE(){ return data.get("emoji_delete"); }

    public static void setPrefix(String value) {
        data.put("prefix",value);
        SQLiteSource.modifyParameter("UPDATE MB_guild_settings_new SET value = ? WHERE parameter LIKE 'prefix';",value);
    }
}
