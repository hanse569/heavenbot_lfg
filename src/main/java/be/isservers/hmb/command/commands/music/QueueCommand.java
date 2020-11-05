package be.isservers.hmb.command.commands.music;

import be.isservers.hmb.command.IPublicCommand;
import be.isservers.hmb.command.PublicCommandContext;
import be.isservers.hmb.lavaplayer.GuildMusicManager;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.utils.HvmAudioTrack_youtube;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;

public class QueueCommand implements IPublicCommand {
    @Override
    public void handle(PublicCommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final BlockingQueue<HvmAudioTrack_youtube> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage("La file d'attente est actuellement vide").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 9);
        final ArrayList<HvmAudioTrack_youtube> trackList = new ArrayList<>(queue);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("\uD83D\uDCDA Liste d'attente");

        StringBuilder description = new StringBuilder();
        for (int i = 0; i < trackCount; i++) {
            final HvmAudioTrack_youtube track = trackList.get(i);
            final AudioTrackInfo info = track.getInfo();

            description.append(getEmoji(i + 1))
                    .append(" `")
                    .append(info.title)
                    .append(" `\n");
        }
        if (trackList.size() > trackCount) {
            description.append("et `")
                    .append(trackList.size() - trackCount)
                    .append("` autres...");
        }
        eb.setDescription(description.toString());

        sendEmbed(channel,eb.build());
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Affiche les chansons en file d'attente";
    }

    private String getEmoji(int number){
        switch (number) {
            case 1:
                return ":one:";
            case 2:
                return ":two:";
            case 3:
                return ":three:";
            case 4:
                return ":four:";
            case 5:
                return ":five:";
            case 6:
                return ":six:";
            case 7:
                return ":seven:";
            case 8:
                return ":eight:";
            case 9:
                return ":nine:";
            default:
                return ":ten:";
        }
    }
}
