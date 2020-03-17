package hv_lfg.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastframe;

    public AudioHandler(AudioPlayer audioPlayer){
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        if (lastframe == null) lastframe = audioPlayer.provide();
        return lastframe != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] data = canProvide() ? lastframe.getData() : null;
        lastframe = null;

        return ByteBuffer.wrap(data);
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
