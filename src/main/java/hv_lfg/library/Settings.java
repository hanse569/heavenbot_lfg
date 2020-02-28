package hv_lfg.library;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @version Provient du Dossier Java LCR 2018-2019
 * @author Reusch Colin
 * @author Hanse Steven
 *
 */
public class Settings {
    private static Properties getFile(){
        Properties prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("settings.properties");
            prop.load(input);
            input.close();
        }
        catch (final IOException e) {
            OutputStream output = null;
            try {
                prop.setProperty("discordToken", "NTUwNjkyOTI0NzE1OTU4Mjgz.XVMwhw.W62qdD2FPvgB7rewV5AmpwfknsY");
                prop.setProperty("idDiscordHeaven", "241110646677176320");
                prop.setProperty("idChannelHeavenBot", "550694482132074506");

                prop.setProperty("emojiTANKfs", "\uD83D\uDEE1");
                prop.setProperty("emojiHEALfs", "\uD83D\uDC89");
                prop.setProperty("emojiDPSfs", "\u2694");
                prop.setProperty("emojiDELETEfs", "\u274C");

                prop.setProperty("emojiTANKreceive", "RE:U+1f6e1");
                prop.setProperty("emojiHEALreceive", "RE:U+1f489");
                prop.setProperty("emojiDPSreceive", "RE:U+2694");
                prop.setProperty("emojiDELETEreceive", "RE:U+274c");


                output = new FileOutputStream("settings.properties");
                prop.store(output, null);
                output.close();
            }
            catch (final IOException ex) { ex.printStackTrace(); }
        }
        return prop;
    }
    private static String getProperty(String name){
        Properties prop = getFile();
        if(prop != null){
            String file = prop.getProperty(name);

            if(!file.isEmpty())
                return file;
            return null;
        }
        return null;
    }

    public static String getDiscordToken(){ return Settings.getProperty("discordToken"); }
    public static String getIdDiscordHeaven(){ return Settings.getProperty("idDiscordHeaven"); }
    public static String getIdChannelHeavenBot(){ return Settings.getProperty("idChannelHeavenBot"); }

    public static String getEmojiTANKforSend(){ return Settings.getProperty("emojiTANKfs"); }//Pour l'envoie du message dans la fonction SendPublicRichEmbed de CreateEventListener
    public static String getEmojiHEALforSend(){ return Settings.getProperty("emojiHEALfs"); }
    public static String getEmojiDPSforSend(){ return Settings.getProperty("emojiDPSfs"); }
    public static String getEmojiDELETEforSend(){ return Settings.getProperty("emojiDELETEfs"); }

    public static String getEmojiTANKforReceive(){ return Settings.getProperty("emojiTANKreceive"); }//Pour l'envoie du message dans la fonction onGuildMessageReactionAdd de RegistrationListener
    public static String getEmojiHEALforReceive(){ return Settings.getProperty("emojiHEALreceive"); }
    public static String getEmojiDPSforReceive(){ return Settings.getProperty("emojiDPSreceive"); }
    public static String getEmojiDELETEforReceive(){ return Settings.getProperty("emojiDELETEreceive"); }

}