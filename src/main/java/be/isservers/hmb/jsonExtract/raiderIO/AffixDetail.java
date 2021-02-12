package be.isservers.hmb.jsonExtract.raiderIO;

import com.fasterxml.jackson.annotation.*;

import java.nio.charset.StandardCharsets;

public class AffixDetail {
    private long id;
    private String name;
    private String description;
    private String wowheadURL;

    @JsonProperty("id")
    public long getID() { return id; }
    @JsonProperty("id")
    public void setID(long value) { this.id = value; }

    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String value) { this.name = toUTF8(value); }

    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String value) { this.description = toUTF8(value); }

    @JsonProperty("wowhead_url")
    public String getWowheadURL() { return wowheadURL; }
    @JsonProperty("wowhead_url")
    public void setWowheadURL(String value) { this.wowheadURL = toUTF8(value); }

    private String toUTF8(String value) {
        return new String(value.getBytes(), StandardCharsets.UTF_8);
    }

    public String getFormattedString(int keyNumber) {
        return "**(+" + Integer.toString(keyNumber) + ") [" + this.getName() + "](" + this.getWowheadURL() + ")**: " + this.getDescription();
        //return "**(+" + Integer.toString(keyNumber) + ") " + this.getName() + "**: " + this.getDescription();
    }


}