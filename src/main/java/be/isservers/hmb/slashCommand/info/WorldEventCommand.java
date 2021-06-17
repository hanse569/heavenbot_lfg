package be.isservers.hmb.slashCommand.info;

import be.isservers.hmb.Config;
import be.isservers.hmb.slashCommand.SlashCommand;
import be.isservers.hmb.slashCommand.SlashCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;

public class WorldEventCommand extends SlashCommand {
    @Override
    public void handle(SlashCommandContext ctx) {
        if (!this.checkEvanChannel(ctx.getEvent(),ctx.getChannel().getId())) return;

        try {
            Document doc = Jsoup.connect("https://fr.wowhead.com").get();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode("#FF7A00"));
            eb.setFooter("Powered by E-Van - La gazette","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");

            Elements events = doc.select("div[id^=EU-group-holiday-]");

            StringBuilder sb = new StringBuilder().append("\n");
            for (Element element : events) {
                Element tmp = element.select("a").first();
                sb.append(tmp.text()).append("\n");
            }
            sb.append("\n");

            eb.setTitle(":newspaper: Evenement mondial de cette semaine");
            eb.setDescription(sb.toString());

            ctx.getEvent().replyEmbeds(eb.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() { return this.GUILD_COMMAND; }

    @Override
    public String getName() {
        return "worldevent";
    }

    @Override
    public String getHelp() {
        return "Indique les world event actuellement présent";
    }
}
