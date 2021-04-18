package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGautoDeleteEvent;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.lfg.LFGmain;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager commandManager = new CommandManager();
    private final WeeklyInfoManager worldEventManager = new WeeklyInfoManager();
    private final int TIME_BETWEEN_AUTO_DELETE = 21600; //toutes les 6h

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
                    LFGmain.Clear(guild.getTextChannelById(Config.getIdChannelDonjon()));
                    LFGdataManagement.heavenDiscord = guild;
                    LFGdataManagement.InitializeOrganizedDate(event);
                    LFGdataManagement.InitializeOrdanizedDateArchived(event);
                    new Timer(TIME_BETWEEN_AUTO_DELETE,new LFGautoDeleteEvent()).start();
                    worldEventManager.Load();
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

        String prefix = Config.getPrefix();
        String raw = event.getMessage().getContentRaw();

        if (raw.equalsIgnoreCase(prefix + "shutdown") && user.getId().equals(Config.getOwnerId())){
            LOGGER.info("Shutting Down");
            Bot.Shutdown();
            return;
        }

        if (raw.startsWith(prefix)){
            commandManager.handle(event,prefix);
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot()){
            return;
        }

        String prefix = Config.getPrefix();
        String raw = event.getMessage().getContentRaw();

        if (raw.startsWith(prefix)){
            commandManager.handle(event,prefix);
        }
        else {
            commandManager.handle(event);
        }
    }
}
