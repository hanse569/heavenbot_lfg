package be.isservers.hmb.weeklyInfo;

import be.isservers.hmb.Config;
import be.isservers.hmb.jsonExtract.raiderIO.Converter;
import be.isservers.hmb.jsonExtract.raiderIO.MythicPlus;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.HttpRequest;

import java.io.IOException;

public class Affixes extends WeeklyInfo {

    public Affixes(){
        super();
    }

    @SuppressWarnings("ConstantConditions")
    public void run() {
        try {
            MythicPlus data = Converter.fromJsonString(HttpRequest.get("https://raider.io/api/v1/mythic-plus/affixes?region=eu&locale=fr"));

            String title = data.getTitle();
            String affix1 = data.getAffixDetails()[0].getFormattedString(2);
            String affix2 = data.getAffixDetails()[1].getFormattedString(4);
            String affix3 = data.getAffixDetails()[2].getFormattedString(7);
            String affix4 = data.getAffixDetails()[3].getFormattedString(10);

            String sb = "Mise à jour des affixes mythiques+ hebdomadaires \n\n" +
                    affix1 + "\n\n" +
                    affix2 + "\n\n" +
                    affix3 + "\n\n" +
                    affix4 + "\n\n";

            this.setEmbedTitle(":fire: " + title);
            this.setEmbedDescription(sb);

            LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelGazette()).sendMessage(this.getEmbedMessage()).queue();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
