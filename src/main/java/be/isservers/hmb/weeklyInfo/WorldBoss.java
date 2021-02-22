package be.isservers.hmb.weeklyInfo;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.MessageUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WorldBoss extends WeeklyInfo {

    public WorldBoss() {
        super();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("https://fr.wowhead.com").get();

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

            this.setEmbedTitle(":skull: Boss mondial de cette semaine");
            this.setEmbedDescription(sb.toString());

            LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelGazette()).sendMessage(this.getEmbedMessage()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
