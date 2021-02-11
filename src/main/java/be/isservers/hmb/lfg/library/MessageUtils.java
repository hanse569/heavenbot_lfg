package be.isservers.hmb.lfg.library;

import be.isservers.hmb.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class MessageUtils {
    public static void SendPrivateMessage(User user, String message){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(message).queue() );
    }

    public static void SendPrivateRichEmbed(User user, EmbedBuilder embedBuilder){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

    public static void SendPublicMessage(TextChannel tc, String message){
        tc.sendMessage(message).queue();
    }

    public static void SendPublicRichEmbed(TextChannel tc, MessageEmbed me){
        tc.sendMessage(me).queue();
    }

    @SuppressWarnings("ConstantConditions")
    public static void SendPublicMessage(JDA jda, String message){
        SendPublicMessage(jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()),message);
    }

    @SuppressWarnings("ConstantConditions")
    public static void SendPublicRichEmbed(JDA jda,OrganizedDate od){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()).sendMessage(od.getEmbedBuilder().build()).queue( (message) -> {
            if(od.getIdMessageDiscord() == null)od.setIdMessageDiscord(message.getId());
        });
    }
}
