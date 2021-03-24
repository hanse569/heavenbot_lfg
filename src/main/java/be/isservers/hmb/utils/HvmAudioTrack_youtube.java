package be.isservers.hmb.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import net.dv8tion.jda.api.entities.User;

public class HvmAudioTrack_youtube extends YoutubeAudioTrack {

    private final User author;
    private long timecode;

    public HvmAudioTrack_youtube(YoutubeAudioTrack yat,User author) {
        super(yat.getInfo(), (YoutubeAudioSourceManager) yat.getSourceManager());
        this.author = author;
        this.timecode = 0;
    }

    public HvmAudioTrack_youtube(YoutubeAudioTrack yat,User author,long timecode) {
        super(yat.getInfo(), (YoutubeAudioSourceManager) yat.getSourceManager());
        this.author = author;
        this.timecode = timecode;
    }

    public User get_hvm_author() {
        return this.author;
    }
    public long getTimecode() { return timecode; }
}
