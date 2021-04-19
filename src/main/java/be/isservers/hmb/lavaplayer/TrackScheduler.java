package be.isservers.hmb.lavaplayer;

import be.isservers.hmb.Bot;
import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.LFGdataManagement;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue;

    TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (track instanceof HmbYoutubeAudioTrack) {
            track.setPosition(((HmbYoutubeAudioTrack) track).getTimecode());
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
        if(queue.isEmpty()) {
            player.destroy();
            Bot.jda.getGuilds().get(0).getAudioManager().closeAudioConnection();
        }
    }

}
