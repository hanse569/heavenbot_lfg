package be.isservers.hmb.lavaplayer;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import net.dv8tion.jda.api.entities.User;

public class HmbYoutubeAudioTrack extends YoutubeAudioTrack {

    private User author;
    private long timecode;

    public HmbYoutubeAudioTrack(YoutubeAudioTrack yat, User author) {
        super(yat.getInfo(), (YoutubeAudioSourceManager) yat.getSourceManager());
        this.author = author;
        this.timecode = 0;
    }

    public HmbYoutubeAudioTrack(YoutubeAudioTrack yat, User author, long timecode) {
        super(yat.getInfo(), (YoutubeAudioSourceManager) yat.getSourceManager());
        this.author = author;
        this.timecode = timecode;
    }

    public User getAuthor() { return author; }
    public long getTimecode() { return timecode; }
}
