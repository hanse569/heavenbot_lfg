package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGemoteManagement;
import be.isservers.hmb.lfg.LFGmain;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class Bot {
    private Bot() throws LoginException {

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
            .addEventListeners(new LFGmain())
            .addEventListeners(new LFGemoteManagement())
            .setActivity(Activity.listening("?help"))
            .build();

    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }

}
