package be.isservers.hmb.weeklyInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WeeklyInfo extends TimerTask {

    private final EmbedBuilder eb = new EmbedBuilder();

    private final static long ONCE_PER_WEEK = 1000*60*60*24*7;
    private final static int NINE_AM = 9;
    private final static int FIFTEEN_MINUTES = 15;

    WeeklyInfo(){
        eb.setColor(Color.decode("#FF7A00"));
        eb.setFooter("Powered by E-Van - La gazette","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");
    }

    public void setEmbedTitle (String title) {
        eb.setTitle(title);
    }

    public void setEmbedDescription (String description) {
        eb.setDescription(description);
    }

    public MessageEmbed getEmbedMessage() {
        eb.setTimestamp(new Date().toInstant());
        return eb.build();
    }

    public static void start(WeeklyInfo we) {
        Timer timer = new Timer();
        timer.schedule(we,getNextWednesday(),ONCE_PER_WEEK);
    }

    private static Date getNextWednesday(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, NINE_AM);
        cal.set(Calendar.MINUTE, FIFTEEN_MINUTES);
        return cal.getTime();
    }

}
