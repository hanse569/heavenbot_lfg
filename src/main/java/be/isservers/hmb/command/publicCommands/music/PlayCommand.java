package be.isservers.hmb.command.publicCommands.music;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.utils.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("DuplicatedCode")
public class PlayCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        if (!selfVoiceState.inVoiceChannel()) {
            final AudioManager audioManager = ctx.getGuild().getAudioManager();
            final VoiceChannel memberChannel = memberVoiceState.getChannel();

            audioManager.openAudioConnection(memberChannel);
            channel.sendMessageFormat(":thumbsup: `%s` rejoint !",memberChannel.getName()).queue();
        }
        else if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage(":x: Vous devez être sur le même canal vocal que moi pour que cela fonctionne").queue();
            return;
        }

        if (ctx.getArgs().isEmpty()) {
            channel.sendMessage(":x: L'utilisation correcte est `!!Play <youtube link>`").queue();
            return;
        }

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":x: Vous devez être dans un canal vocal pour que cette commande fonctionne").queue();
            return;
        }

        String link = String.join(" ", ctx.getArgs());

        if(!link.startsWith("https://") && !link.startsWith("http://")) {
            try{
                Gson gson = new Gson();
                Map<?, ?> map = gson.fromJson(HttpRequest.get("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=2&key=AIzaSyA4Q81p8z0D484MoPqADdbAwaem51vJdHM&q="+link.replace(" ","+")),Map.class);
                LinkedTreeMap item = (LinkedTreeMap) ((ArrayList)map.get("items")).get(0);
                if (((LinkedTreeMap) item.get("id")).get("kind").equals("youtube#channel")){
                    item = (LinkedTreeMap) ((ArrayList)map.get("items")).get(1);
                }
                link = "https://www.youtube.com/watch?v=" + ((LinkedTreeMap) item.get("id")).get("videoId");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(link.contains("youtu.be/") || link.contains("youtube.com/") || link.contains("twitch.tv/")) {
            PlayerManager.getInstance().loadAndPlay(channel, link, ctx.getAuthor());
        }
        else {
            channel.sendMessage(":x: Seul Youtube, Youtube Music et Twitch sont pris en charge !").queue();
        }
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Joue une musique\n" +
                "Usage: `!!Play <youtube link>`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pl", "start");
    }
}
