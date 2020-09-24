package hv_lfg;

import hv_lfg.library.HttpRequest;
import hv_lfg.music.MusicManager;
import hv_lfg.music.MusicPlayer;
import hv_lfg.youtubeApi.Converter;
import hv_lfg.youtubeApi.YoutubeMusic;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class BotMusic extends ListenerAdapter {

    private final MusicManager manager = new MusicManager();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel textChannel = event.getChannel();
        User user = event.getAuthor();
        Message command = event.getMessage();

        if(!user.isBot()/* && event.getChannel().getId().equals(Settings.getIdChannelHeavenBot())*/){
            if (command.getContentDisplay().startsWith("/play")){
                this.play(guild,textChannel,user,command.getContentDisplay());
            }
            else if (command.getContentDisplay().startsWith("/skip")){
                this.skip(guild,textChannel);
            }
            else if (command.getContentDisplay().startsWith("/clear")){
                this.clear(textChannel);
            }
        }
        else if (command.getContentDisplay().startsWith("/play")){
            command.delete().queue();
        }
    }

    private void play(Guild guild, TextChannel textChannel, User user, String command){

        if(guild == null) return;

        if(!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect()){
            VoiceChannel voiceChannel = guild.getMember(user).getVoiceState().getChannel();
            if(voiceChannel == null){
                textChannel.sendMessage("Vous devez etre connecte à un salon vocal.").queue();
                return;
            }
            guild.getAudioManager().openAudioConnection(voiceChannel);
        }

        //manager.loadTrack(textChannel, command.replaceFirst("/play ", ""));

        String key = command.replaceFirst("play ", "");
        if(key.startsWith("https://")) manager.loadTrack(textChannel, key);
        else {
            try
            {
                key = key.replace(" ","+").replaceFirst("/","");
                String query = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&key=AIzaSyA_d2KclU41UmiYSrBjaEZPsuFRl7UT14s&q="+key;
                YoutubeMusic data = Converter.fromJsonString(HttpRequest.get(query));
                manager.loadTrack(textChannel,"https://www.youtube.com/watch?v=" + data.getVideoId());
                textChannel.sendMessage("/play https://www.youtube.com/watch?v=" + data.getVideoId()).queue();
            }
            catch (IOException e) {
                textChannel.sendMessage("Impossible d'executer la requete vers Youtube").queue();
                return;
            }
        }
    }

    private void skip(Guild guild, TextChannel textChannel){
        if(!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect()){
            textChannel.sendMessage("Le player n'as pas de piste en cours.").queue();
            return;
        }

        manager.getPlayer(guild).skipTrack();
        textChannel.sendMessage("La lecture est passe a la piste suivante.").queue();
    }

    private void clear(TextChannel textChannel){
        MusicPlayer player = manager.getPlayer(textChannel.getGuild());

        if(player.getListener().getTracks().isEmpty()){
            textChannel.sendMessage("Il n'y a pas de piste dans la liste d'attente.").queue();
            return;
        }

        player.getListener().getTracks().clear();
        textChannel.sendMessage("La liste d'attente a ete vide.").queue();
    }
}
