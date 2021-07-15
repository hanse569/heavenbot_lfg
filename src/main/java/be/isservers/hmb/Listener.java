package be.isservers.hmb;

import be.isservers.hmb.lfg.LFGautoDeleteEvent;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.lfg.LFGmain;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.List;
import java.util.Map;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager commandManager = new CommandManager();
    private final SlashCommandManager slashCommandManager = new SlashCommandManager();
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
                    slashCommandManager.Init();

                    for (Command command : guild.retrieveCommands().complete()) {
                        if (command.getName().startsWith("set")) {
                            System.out.println(command.getName() + ": " + command.getId());
                            guild.updateCommandPrivilegesById(command.getId(),CommandPrivilege.enable(guild.getRoleById("486419048150859781"))).queue();
                        }
                    }

                    guild.retrieveCommandPrivileges().queue( commands -> {
                            for (Map.Entry<String, List<CommandPrivilege>> entry : commands.entrySet()){
                                for (CommandPrivilege commandPrivilege : entry.getValue()) {
                                    System.out.println(entry.getKey() + ": " + commandPrivilege.getType() + " | " + commandPrivilege.isEnabled() + " | " + commandPrivilege.getIdLong());
                                }
                            }
                        }
                    );

                }
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        slashCommandManager.handle(event);
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
