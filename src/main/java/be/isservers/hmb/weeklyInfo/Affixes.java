package be.isservers.hmb.weeklyInfo;

import be.isservers.hmb.Config;
import be.isservers.hmb.jsonExtract.raiderIO.AffixDetail;
import be.isservers.hmb.jsonExtract.raiderIO.Converter;
import be.isservers.hmb.jsonExtract.raiderIO.MythicPlus;
import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.utils.HttpRequest;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.Date;

public class Affixes {

    @SuppressWarnings("ConstantConditions")
    public static void Load() {

        try {
            MythicPlus data = Converter.fromJsonString(HttpRequest.get("https://raider.io/api/v1/mythic-plus/affixes?region=eu&locale=fr"));
            LFGdataManagement.heavenDiscord.getTextChannelById(Config.getIdChannelGazette()).sendMessage(ConstructEmbed(data).build()).queue();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static EmbedBuilder ConstructEmbed(MythicPlus data) {
        String title = data.getTitle();
        String affix1 = data.getAffixDetails()[0].getFormattedString(2);
        String affix2 = data.getAffixDetails()[1].getFormattedString(4);
        String affix3 = data.getAffixDetails()[2].getFormattedString(7);
        String affix4 = data.getAffixDetails()[3].getFormattedString(10);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(":fire: " + title);
        eb.setColor(Color.decode("#FF7A00"));
        String sb = "Mise à jour des affixes mythiques+ hebdomadaires \n\n" +
                affix1 + "\n\n" +
                affix2 + "\n\n" +
                affix3 + "\n\n" +
                affix4 + "\n\n";
        eb.setDescription(sb);
        eb.setTimestamp(new Date().toInstant());
        eb.setFooter("Powered by E-Van - Information Donjon","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");

        return eb;
    }
}
