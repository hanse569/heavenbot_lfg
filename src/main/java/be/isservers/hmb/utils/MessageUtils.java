package be.isservers.hmb.utils;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.library.OrganizedDate;
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
        SendPublicMessage(jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelDonjon()),message);
    }

    @SuppressWarnings("ConstantConditions")
    public static void SendPublicRichEmbed(JDA jda, OrganizedDate od){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelDonjon()).sendMessage(od.getEmbedBuilder().build()).queue( (message) -> {
            if(od.getIdMessageDiscord() == null)od.setIdMessageDiscord(message.getId());
        });
    }

    public static String Italics(String value) {
        return "*" + value + "*";
    }

    public static String Bold(String value) {
        return "**" + value + "**";
    }

    public static String BoldItalics(String value) {
        return "***" + value + "***";
    }

    public static String Underline(String value) {
        return "__" + value + "__";
    }

    public static String UnderlineItalics(String value) {
        return "__*" + value + "*__";
    }

    public static String UnderlineBold(String value) {
        return "**" + value + "**";
    }

    public static String UnderlineBoldItalics(String value) {
        return "__***" + value + "***__";
    }

    public static String Strikethrough(String value) {
        return "~~" + value + "~~";
    }
}
