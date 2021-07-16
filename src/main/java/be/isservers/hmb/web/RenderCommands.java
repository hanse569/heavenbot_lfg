package be.isservers.hmb.web;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RenderCommands implements Render {
    private final String prefix = "?";
    private final String titre;
    private final String alias;
    private final JsonArray listItem;

    public RenderCommands(JsonObject jo) {
        this.titre = jo.getString("title");
        this.alias = jo.getString("alias");
        this.listItem = jo.getJsonArray("commands");
    }

    @Override
    public String build() throws IOException {
        String templateItem = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/commandItem.peb").getFile()).toPath()));

        templateItem = templateItem.replace("{{title}}", this.titre);
        if (alias.length() > 0) {
            templateItem = templateItem.replace("{{alias}}", "<small style='font-style: italic'>Alias: " + this.alias + "</small>");
        }
        else {
            templateItem = templateItem.replace("{{alias}}", "");
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (;i < listItem.size()-1;i++) {
            sb.append(buildSubItem(listItem.getJsonObject(i)));
            sb.append("<hr>");
        }
        sb.append(buildSubItem(listItem.getJsonObject(i)));
        templateItem = templateItem.replace("{{commands}}",sb.toString());

        return templateItem;
    }

    private String buildSubItem(JsonObject jo) throws IOException {
        String templateSubItem = new String(Files.readAllBytes(new File(getClass().getClassLoader().getResource("pages/item/commandSubItem.peb").getFile()).toPath()));

        templateSubItem = templateSubItem.replace("{{prefix}}",this.prefix);
        templateSubItem = templateSubItem.replace("{{command}}",jo.getString("title") + "{{args}}");
        templateSubItem = templateSubItem.replace("{{description}}",jo.getString("des"));

        if (jo.getString("args").length() > 0) {
            templateSubItem = templateSubItem.replace("{{args}}", "<span class='btn-sm btn-dark' style='padding: 0 4px; margin-left: 4px;'>" + jo.getString("args") + "</span>");
        }
        else {
            templateSubItem = templateSubItem.replace("{{args}}","");
        }

        return templateSubItem;
    }
}