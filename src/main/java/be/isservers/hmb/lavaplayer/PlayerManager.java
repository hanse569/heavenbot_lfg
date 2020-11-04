package be.isservers.hmb.lavaplayer;

import be.isservers.hmb.utils.HvmAudioTrack_youtube;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(),(guildid) -> {
           final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

           guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

           return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel,String trackUrl, User author) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        final User final_author = author;

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                HvmAudioTrack_youtube audioTrackYoutube = new HvmAudioTrack_youtube((YoutubeAudioTrack) track,final_author);
                musicManager.scheduler.queue(audioTrackYoutube);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(audioTrackYoutube.getInfo().title,audioTrackYoutube.getInfo().uri);
                eb.setThumbnail("https://img.youtube.com/vi/"+audioTrackYoutube.getIdentifier()+"/1.jpg");
                eb.setAuthor("\u2705 Ajouté à la file d'attente");
                channel.sendMessage(eb.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                for (final AudioTrack track : tracks) {
                    HvmAudioTrack_youtube audioTrackYoutube = new HvmAudioTrack_youtube((YoutubeAudioTrack) track,final_author);
                    musicManager.scheduler.queue(audioTrackYoutube);
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(playlist.getName());
                eb.setDescription("Ajouté par " + final_author.getName());
                eb.setAuthor("\u2705 Playlist ajouté à la file d'attente");
                channel.sendMessage(eb.build()).queue();
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {
            }
        });
    }

    public static PlayerManager getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
