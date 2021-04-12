package be.isservers.hmb.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.List;

public class CommandContext {
    private final GuildMessageReceivedEvent guildEvent;
    private final PrivateMessageReceivedEvent privateEvent;
    private final List<String> args;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
        this.guildEvent = event;
        this.privateEvent = null;
        this.args = args;
    }

    public CommandContext(PrivateMessageReceivedEvent event, List<String> args) {
        this.guildEvent = null;
        this.privateEvent = event;
        this.args = args;
    }

    public Guild getGuild() {
        if (guildEvent != null)
            return this.getGuildEvent().getGuild();
        return null;
    }

    public TextChannel getChannel() {
        if (guildEvent != null)
            return this.getGuildEvent().getChannel();
        return null;
    }

    public Member getMember() {
        if (guildEvent != null)
            return this.getGuildEvent().getMember();
        return null;
    }

    public Member getSelfMember() {
        if (guildEvent != null)
            return this.getGuildEvent().getGuild().getSelfMember();
        return null;
    }

    public User getAuthor() {
        if (guildEvent != null)
            return this.getGuildEvent().getAuthor();
        return null;
    }

    public JDA getJDA() {
        if (guildEvent != null) {
            return guildEvent.getJDA();
        }
        else if (privateEvent != null) {
            return privateEvent.getJDA();
        }
        return null;
    }

    public GuildMessageReceivedEvent getGuildEvent () {
        return guildEvent;
    }

    public PrivateMessageReceivedEvent getPrivateEvent () {
        return privateEvent;
    }

    public List<String> getArgs() {
        return this.args;
    }
}
