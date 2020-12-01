package be.isservers.hmb.command.publicCommands.music;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.utils.HttpRequest;
import be.isservers.hmb.youtubeApi.Converter;
import be.isservers.hmb.youtubeApi.YoutubeMusic;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class PlayCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        if (ctx.getArgs().isEmpty()) {
            channel.sendMessage(":x: L'utilisation correcte est `!!Play <youtube link>`").queue();
            return;
        }

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
             channel.sendMessage(":x: J'ai besoin d'être dans un canal vocal pour que cela fonctionne").queue();
             return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(":x: Vous devez être dans un canal vocal pour que cette commande fonctionne").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage(":x: Vous devez être sur le même canal vocal que moi pour que cela fonctionne").queue();
            return;
        }

        String link = String.join(" ", ctx.getArgs());

        //if (!isUrl(link)) {
        if(!link.startsWith("https://")) {
            try{
                StringBuilder sb = new StringBuilder();
                for (String arg : ctx.getArgs()) {
                    sb.append(arg).append(" ");
                }
                String query = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&key=AIzaSyA4Q81p8z0D484MoPqADdbAwaem51vJdHM&q="+link.replace(" ","+");
                YoutubeMusic data = Converter.fromJsonString(HttpRequest.get(query));
                link = "https://www.youtube.com/watch?v=" + data.getVideoId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, ctx.getAuthor());
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

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
