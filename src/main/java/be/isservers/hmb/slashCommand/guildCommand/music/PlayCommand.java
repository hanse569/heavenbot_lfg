package be.isservers.hmb.slashCommand.guildCommand.music;

import be.isservers.hmb.lavaplayer.PlayerManager;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import be.isservers.hmb.slashCommand.SlashCommandParamaterItem;
import be.isservers.hmb.slashCommand.SlashCommandParameter;
import be.isservers.hmb.utils.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayCommand extends SlashCommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandContext ctx) {
        final GuildVoiceState selfVoiceState = ctx.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;

        if (!selfVoiceState.inVoiceChannel()) {
            final AudioManager audioManager = ctx.getGuild().getAudioManager();
            final VoiceChannel memberChannel = memberVoiceState.getChannel();

            audioManager.openAudioConnection(memberChannel);
            ctx.getEvent().replyFormat(":thumbsup: `%s` rejoint !",memberChannel.getName()).queue();
        }
        else if (!checkSameVoiceChannel(ctx.getEvent(),selfVoiceState,memberVoiceState)) return;

        if (!checkMemberInVoiceChannel(ctx.getEvent(),memberVoiceState)) return;

        String link = ctx.getArgs().get(0);

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
            PlayerManager.getInstance().loadAndPlay(ctx.getChannel(), link, ctx.getAuthor());
        }
        else {
            ctx.getEvent().reply(":x: Seul Youtube, Youtube Music et Twitch sont pris en charge !").queue();
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Ajoute une musique via un mot-clé ou bien un lien (Youtube, Youtube Music et Twitch)";
    }

    @Override
    public int getType() {
        return SlashCommand.GUILD_COMMAND;
    }

    @Override
    public List<SlashCommandParamaterItem> getParam() {
        SlashCommandParameter scp = new SlashCommandParameter()
                .add(OptionType.STRING,"input","A search term or link",true);
        return scp.build();
    }
}
