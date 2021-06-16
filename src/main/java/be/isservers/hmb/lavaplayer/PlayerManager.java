package be.isservers.hmb.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);
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
                long timecode = 0;
                if(trackUrl.contains("?t=")){
                    String buffer = trackUrl.substring(trackUrl.indexOf("?t=")+3);
                    timecode = Long.parseLong(buffer) * 1000;
                }

                AudioTrack at = null;
                if (track instanceof YoutubeAudioTrack) {
                    at = new HmbYoutubeAudioTrack((YoutubeAudioTrack) track,final_author,timecode);
                }
                else if (track instanceof TwitchStreamAudioTrack) {
                    at = new HmbTwitchAudioTrack((TwitchStreamAudioTrack) track,final_author);
                }


                if (at != null){
                    musicManager.scheduler.queue(at);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(at.getInfo().title,at.getInfo().uri);
                    if (at instanceof HmbYoutubeAudioTrack) {
                        eb.setThumbnail("https://img.youtube.com/vi/"+ at.getIdentifier()+"/1.jpg");
                    }
                    eb.setAuthor("\u2705 Ajouté à la file d'attente");
                    channel.sendMessageEmbeds(eb.build()).queue();
                }
                else {
                    LOGGER.error("Pas trouvé l'instance of");
                    channel.sendMessage(":x: Seul Youtube, Youtube Music et Twitch (Audio uniquement) sont pris en charge !").queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                for (final AudioTrack track : tracks) {
                    HmbYoutubeAudioTrack audioTrackYoutube = new HmbYoutubeAudioTrack((YoutubeAudioTrack) track,final_author);
                    musicManager.scheduler.queue(audioTrackYoutube);
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(playlist.getName());
                eb.setDescription("Ajouté par " + final_author.getName());
                eb.setAuthor("\u2705 Playlist ajouté à la file d'attente");
                channel.sendMessageEmbeds(eb.build()).queue();
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
