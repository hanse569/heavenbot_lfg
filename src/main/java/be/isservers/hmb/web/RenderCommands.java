package be.isservers.hmb.web;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RenderCommands extends Render {
    private final String prefix = "?";
    private final String titre;
    private final String alias;
    private final JsonArray listItem;

    public RenderCommands(JsonObject jo) {
        this.titre = jo.getString("title");
        this.alias = jo.getString("alias");
        this.listItem = jo.getJsonArray("commands");
    }

    public String printCommand() {
        String sb = this.cardHeader(this.titre, this.alias) + this.cardBody(listItem);
        return this.surround("div class='card'", sb);
    }

    private String cardHeader(String titre,String alias){
        String content = "";
        if (alias.length() > 0) {
            content = surround("small style='font-style: italic'","Alias: " + alias);
        }
        return surround("div class='card-header'", surround("h4",titre) + content);
    }

    private String cardBody(JsonArray list) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (;i < list.size()-1;i++) {
            sb.append(cardBodyItem(list.getJsonObject(i)));
            sb.append(this.surround("hr"));
        }
        sb.append(cardBodyItem(list.getJsonObject(i)));
        return this.surround("div class='card-body'",sb.toString());
    }

    private String cardBodyItem(JsonObject jo) {
        String content = "";
        if (jo.getString("args").length() > 0) {
            content = surround("span class='btn-sm btn-dark' style='padding: 0 4px; margin-left: 4px;'",jo.getString("args"));
        }
        return this.surround("h6",this.prefix + jo.getString("title") + content) + this.surround("p class='text-muted'",jo.getString("des"));
    }

}