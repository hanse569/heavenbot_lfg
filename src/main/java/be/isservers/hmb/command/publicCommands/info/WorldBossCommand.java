package be.isservers.hmb.command.publicCommands.info;

import be.isservers.hmb.Config;
import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class WorldBossCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        try {
            Document doc = Jsoup.connect("https://fr.wowhead.com").get();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode("#FF7A00"));
            eb.setFooter("Powered by E-Van - La gazette","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");

            Elements eventsSL = doc.select("div[id^=EU-group-epiceliteworldsl-line-]");
            StringBuilder sb = new StringBuilder().append("\n");
            for (Element element : eventsSL) {
                Element tmp = element.select("a").first();
                sb.append(MessageUtils.Bold("Shadowlands: ")).append(tmp.text()).append("\n");
            }
            sb.append("\n");

            Elements eventsBFA = doc.select("div[id^=EU-group-epiceliteworldbfa-line-]");
            for (Element element : eventsBFA) {
                Element tmp = element.select("a").first();
                sb.append(MessageUtils.Bold("BFA: ")).append(tmp.text()).append("\n");
            }
            sb.append("\n");

            Elements eventsLegion = doc.select("div[id^=EU-group-epiceliteworld-line-]");
            for (Element element : eventsLegion) {
                Element tmp = element.select("a").first();
                sb.append(MessageUtils.Bold("Legion: ")).append(tmp.text()).append("\n");
            }
            sb.append("\n");

            eb.setTitle(":skull: Boss mondial de cette semaine");
            eb.setDescription(sb.toString());

            ctx.getChannel().sendMessage(eb.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "worldboss";
    }

    @Override
    public String getHelp() {
        return "Indique les world boss actuellement présent pour le contenu de Shadowlands, Battle For Azeroth et Légion";
    }

    @Override
    public List<String> getAliases() {
        return List.of("wb");
    }
}
