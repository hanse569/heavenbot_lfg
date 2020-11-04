package be.isservers.hmb;

import be.isservers.hmb.database.SQLiteDataSource;
import be.isservers.hmb.lfg.LFGcel;
import be.isservers.hmb.lfg.LFGrl;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class Bot {
    private Bot() throws LoginException, SQLException {
        SQLiteDataSource.getConnection();

        EmbedUtils.setEmbedBuilder(
            () -> new EmbedBuilder()
            .setColor(0x3883d9)
            .setFooter("HeavenBot - /!\\ WORK IN PROGRESS /!\\")
        );

        JDABuilder.createDefault(
            Config.get("token"),
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_EMOJIS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.DIRECT_MESSAGES
        )
            .disableCache(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.ACTIVITY,
                CacheFlag.EMOTE
            )
            .enableCache(CacheFlag.VOICE_STATE)
            .addEventListeners(new Listener())
            .addEventListeners(new LFGcel())
            .addEventListeners(new LFGrl())
            .setActivity(Activity.watching("pour débuguer la version finale de E-Van"))
            .build();

    }

    public static void main(String[] args) throws LoginException, SQLException {
        new Bot();
    }

}
