package be.isservers.hmb.weeklyInfo;

import be.isservers.hmb.Config;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Map;

public class Affixes extends WeeklyInfo {

    public Affixes(){
        super();
    }

    @SuppressWarnings("ConstantConditions")
    public void run() {
        String[] data =  this.getAffixes();

        if (data != null){
            String title = data[0];
            String affix1 = data[1];
            String affix2 = data[2];
            String affix3 = data[3];
            String affix4 = data[4];

            String sb = "Mise à jour des affixes mythiques+ hebdomadaires \n\n" +
                    affix1 + "\n\n" +
                    affix2 + "\n\n" +
                    affix3 + "\n\n" +
                    affix4 + "\n\n";

            this.setEmbedTitle(":fire: " + title);
            this.setEmbedDescription(sb);

            LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelGazette()).sendMessageEmbeds(this.getEmbedMessage()).queue();
        }
    }

    private String[] getAffixes(){
        try {
            int[] keyNumber = {2,4,7,10};
            String[] buffer = new String[keyNumber.length + 1];
            int i = 0;

            Gson gson = new Gson();
            Map<?, ?> map = gson.fromJson(HttpRequest.get("https://raider.io/api/v1/mythic-plus/affixes?region=eu&locale=fr"),Map.class);

            buffer[i] = map.get("title").toString();
            ArrayList data = (ArrayList) map.get("affix_details");
            for (Object o : data) {
                i++;
                buffer[i] = getFormattedString(keyNumber[i-1],((LinkedTreeMap)o).get("name"),((LinkedTreeMap)o).get("wowhead_url"),((LinkedTreeMap)o).get("description"));
            }
            return buffer;
        } catch (Exception ex) {
            return null;
        }
    }

    private String getFormattedString(int keyNumber, Object name, Object wowheadUrl, Object description) {
        return "**(+" + keyNumber + ") [" + name + "](" + wowheadUrl + ")**: " + description;
    }
}
