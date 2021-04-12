package be.isservers.hmb.lavaplayer;

import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import net.dv8tion.jda.api.entities.User;

public class HmbTwitchAudioTrack extends TwitchStreamAudioTrack {

    private User author;

    public HmbTwitchAudioTrack(TwitchStreamAudioTrack yat, User author) {
        super(yat.getInfo(), (TwitchStreamAudioSourceManager) yat.getSourceManager());
        this.author = author;
    }

    public User getAuthor() { return author; }
}
