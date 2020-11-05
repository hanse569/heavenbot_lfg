package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.lfg.LFGmain;
import be.isservers.hmb.utils.SQLiteSource;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @SuppressWarnings({"ConstantConditions", "PlaceholderCountMatchesArgumentCount"})
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());

        if(event.getJDA().getGuilds().isEmpty()){
            LOGGER.info("This bot is not on any guilds!", event.getJDA().getSelfUser().getAsTag());
        }
        else{
            for (Guild guild : event.getJDA().getGuilds()){
                if(guild.getId().equals(Config.getIdDiscordHeaven())){
                    LOGGER.info("Connected to " + guild.getName(), event.getJDA().getSelfUser().getAsTag());
                    LFGmain.Clear(guild.getTextChannelById(Config.getIdChannelHeavenBot()));
                    LFGdataManagement.heavenDiscord = guild;
                    LFGdataManagement.InitializeOrganizedDate(event);
                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()){
            return;
        }

        final long guildId = event.getGuild().getIdLong();
        String prefix = VeryBadDesign.PREFIXES.computeIfAbsent(guildId, this::getPrefix);
        String raw = event.getMessage().getContentRaw();

        if (raw.equalsIgnoreCase(prefix + "shutdown") && user.getId().equals(Config.get("owner_id"))){
            LOGGER.info("Shutting Down");
            event.getJDA().shutdown();
            BotCommons.shutdown(event.getJDA());
            return;
        }

        if (raw.startsWith(prefix)){
            manager.handle(event,prefix);
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot()){
            return;
        }

        String prefix = "!";
        String raw = event.getMessage().getContentRaw();

        if (raw.startsWith(prefix)){
            manager.handle(event,prefix);
        }
        else {
            manager.handle(event);
        }
    }

    private String getPrefix(long guildId){
        try (final PreparedStatement preparedStatement = SQLiteSource
                .getConn()
                .prepareStatement("SELECT prefix FROM MB_guild_settings WHERE guild_id = ?")){

            preparedStatement.setString(1,String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getString("prefix");
                }
            }
            
            try (final PreparedStatement insertStatement  = SQLiteSource
                    .getConn()
                    .prepareStatement("INSERT INTO MB_guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));
                insertStatement.execute();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return Config.get("prefix");
    }
}
