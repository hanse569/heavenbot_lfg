package be.isservers.hmb.lfg.library;

import be.isservers.hmb.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class MessageUtils {
    public static void SendPrivateMessage(User user){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage("__*Syntaxe: !lfg <raid|donjon|bg|arene>*__").queue() );
    }

    public static void SendPrivateRichEmbed(User user, EmbedBuilder embedBuilder){
        user.openPrivateChannel().queue( (channel) -> channel.sendMessage(embedBuilder.build()).queue());
    }

    @SuppressWarnings("ConstantConditions")
    public static void SendPublicMessage(JDA jda, String message){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()).sendMessage(message).queue();
    }

    @SuppressWarnings("ConstantConditions")
    public static void SendPublicRichEmbed(JDA jda,OrganizedDate od){
        jda.getGuildById(Config.getIdDiscordHeaven()).getTextChannelById(Config.getIdChannelHeavenBot()).sendMessage(od.getEmbedBuilder().build()).queue( (message) ->
        {
            message.addReaction(Config.getEmojiTANK()).queue();
            message.addReaction(Config.getEmojiHEAL()).queue();
            message.addReaction(Config.getEmojiDPS()).queue();
            message.addReaction(Config.getEmojiDELETE()).queue();
        });
    }
}
