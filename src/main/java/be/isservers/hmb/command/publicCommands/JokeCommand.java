package be.isservers.hmb.command.publicCommands;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import com.fasterxml.jackson.databind.JsonNode;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class JokeCommand implements ICommand {
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        if (!ctx.getChannel().getId().equals(Config.getIdChannelEvan())){
            return;
        }

        WebUtils.ins.getJSONObject("https://apis.duncte123.me/joke").async((json) -> {
            if (!json.get("success").asBoolean()){
                channel.sendMessage(":x: Une erreur s'est produite, réessayez plus tard").queue();
                System.out.println(json);
                return;
            }

            final JsonNode data = json.get("data");
            final String title = data.get("title").asText();
            final String url = data.get("url").asText();
            final String body = data.get("body").asText();
            final EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(title,url)
                    .setDescription(body);

            channel.sendMessage(embed.build()).queue();
        });
    }

    @Override
    public int getType() {
        return this.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "joke";
    }

    @Override
    public String getHelp() {
        return ":x: Une erreur s'est produite, réessayez plus tard";
    }
}
