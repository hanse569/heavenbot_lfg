package be.isservers.hmb.weeklyInfo;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.LFGdataManagement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WorldEvent extends WeeklyInfo {

    public WorldEvent() {
        super();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("https://fr.wowhead.com").get();
            Elements events = doc.select("div[id^=EU-group-holiday-]");

            StringBuilder sb = new StringBuilder().append("\n");
            for (Element element : events) {
                Element tmp = element.select("a").first();
                sb.append(tmp.text()).append("\n");
            }
            sb.append("\n");

            this.setEmbedTitle(":newspaper: Evenement mondial de cette semaine");
            this.setEmbedDescription(sb.toString());

            LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelGazette()).sendMessage(this.getEmbedMessage()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
