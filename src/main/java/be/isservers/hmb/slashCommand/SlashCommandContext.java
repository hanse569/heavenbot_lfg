package be.isservers.hmb.slashCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandContext {
    private final SlashCommandEvent slashCommandEvent;
    private final List<String> args = new ArrayList<>();

    public SlashCommandContext(SlashCommandEvent event) {
        this.slashCommandEvent = event;
        for (OptionMapping option : event.getOptions()) {
            this.args.add(option.getAsString());
        }
        System.out.println(args);
    }

    public Guild getGuild() {
        return slashCommandEvent.getGuild();
    }

    public TextChannel getChannel() {
        return slashCommandEvent.getTextChannel();
    }

    public Member getMember() {
        return slashCommandEvent.getMember();
    }

    public Member getSelfMember() {
        return slashCommandEvent.getGuild().getSelfMember();
    }

    public User getAuthor() {
        return slashCommandEvent.getUser();
    }

    public JDA getJDA() {
        return slashCommandEvent.getJDA();
    }

    public SlashCommandEvent getEvent () {
        return slashCommandEvent;
    }

    public List<String> getArgs() {
        return this.args;
    }
}
