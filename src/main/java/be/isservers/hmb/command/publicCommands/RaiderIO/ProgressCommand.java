package be.isservers.hmb.command.publicCommands.RaiderIO;

import be.isservers.hmb.command.CommandContext;
import be.isservers.hmb.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;

public class ProgressCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Rheria <Hëaven> @ Elune (EU)");
        eb.setImage("http://isservers.be/rheria2.png");
        eb.setColor(Color.decode("#FF7A00"));
        eb.setFooter("Powered by E-Van - La gazette","https://cdn.discordapp.com/app-icons/550692924715958283/07edcffb72e15c040daf868e86496d73.png");

        eb.addField("Raid Progression","Castle Nathria 10/10 HM",true);
        eb.addBlankField(true);
        eb.addField("External links","[Armory](https://worldofwarcraft.com/en-gb/character/eu/elune/Rheria/)\n" + "[Raider.IO](https://raider.io/characters/eu/elune/Rheria)\n" + "[Warcraftlogs](https://raider.io/characters/eu/elune/Rheria)",true);
        eb.addField("Covenant",":wow: Kyrian (40)",true);
        eb.addBlankField(true);
        eb.addField("Role",":crossed_swords: DPS",true);
        eb.setTimestamp(Calendar.getInstance().toInstant());

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();

        try {
            Document document = Jsoup.connect("http://evan.isservers.be").get();
            Element img_url = document.select("canvas[id=guild-tabard]").first();
            System.out.println(img_url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        functionTest(ctx);
    }

    private void functionTest(CommandContext ctx) {
        BufferedImage image = new BufferedImage(670, 420, BufferedImage.TYPE_INT_ARGB);
        Font font, fontBold;

        font = Font.getFont(Font.SANS_SERIF);
        fontBold = Font.getFont(Font.MONOSPACED);

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(45, 92, 250));
        g2d.fillRect(0, 35, 510, 420);

        g2d.setColor(new Color(25, 62, 200));
        g2d.fillRect(0, 0, 510, 35);


        try {
            File tempFile = tempFileFromImage(image, "game-c4", ".png");

            Message msg = ctx.getChannel().sendFile(tempFile,("test.png")).complete();

        } catch(IOException e) {
            System.out.println("Couldn't send image:" + e);
        }
    }

    public static File tempFileFromImage(BufferedImage image, String prefix, String suffix) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return tempFileFromInputStream(is, prefix, suffix);
    }

    public static File tempFileFromInputStream(InputStream is, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();

        try(FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(is, out);
        }

        return tempFile;
    }

    @Override
    public int getType() {
        return ICommand.PUBLIC_COMMAND;
    }

    @Override
    public String getName() {
        return "progress";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
