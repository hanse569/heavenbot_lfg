package be.isservers.hmb.web;

import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RenderLogs implements Render {
    private final String id;
    private final String title;
    private final String author;
    private final String date;

    public RenderLogs(JsonObject entries) {
        this.id = entries.getString("id");
        this.title = entries.getString("title");
        this.author = entries.getString("owner");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(entries.getString("start")));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.date = sdf.format(cal.getTime());
    }

    @Override
    public String build() throws IOException {
        String template = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/logItem.peb").getFile()).toPath()));

        template = template.replace("{{title}}", this.title);
        template = template.replace("{{author}}", this.author);
        template = template.replace("{{date}}", this.date);
        template = template.replace("{{id}}", this.id);
        return template;
    }
}