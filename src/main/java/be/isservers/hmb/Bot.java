package be.isservers.hmb;

import be.isservers.hmb.api.ApiVerticle;
import be.isservers.hmb.lfg.LFGemoteManagement;
import be.isservers.hmb.lfg.LFGmain;
import io.vertx.core.Vertx;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot {

    public static JDA jda;

    private Bot() throws LoginException {

        Config.Initialize();

        jda = JDABuilder.createDefault(
            Config.getToken(),
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
            .setChunkingFilter(ChunkingFilter.ALL)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(new Listener())
            .addEventListeners(new LFGmain())
            .addEventListeners(new LFGemoteManagement())
            .setActivity(Activity.listening("?help"))
            .build();

    }

    public static void Shutdown() {
        jda.shutdown();
        jda.getHttpClient().connectionPool().evictAll();
        jda.getHttpClient().dispatcher().executorService().shutdown();
        Vertx.vertx().close();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ApiVerticle());
    }

}
