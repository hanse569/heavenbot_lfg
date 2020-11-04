package be.isservers.hmb;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }

    public static String getIdDiscordHeaven(){ return get("iddiscordheaven"); }
    public static String getIdChannelHeavenBot(){ return get("idchannelheavenbot"); }

    public static String getEmojiTANK(){ return get("EMOJITANK"); }//Pour l'envoie du message dans la fonction SendPublicRichEmbed de CreateEventListener
    public static String getEmojiHEAL(){ return get("EMOJIHEAL"); }
    public static String getEmojiDPS(){ return get("EMOJIDPS"); }
    public static String getEmojiDELETE(){ return get("EMOJIDELETE"); }
}
