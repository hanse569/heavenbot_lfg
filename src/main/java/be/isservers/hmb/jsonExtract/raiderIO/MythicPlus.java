package be.isservers.hmb.jsonExtract.raiderIO;

import com.fasterxml.jackson.annotation.*;

import java.nio.charset.StandardCharsets;

public class MythicPlus {
    private String region;
    private String title;
    private String leaderboardURL;
    private AffixDetail[] affixDetails;

    @JsonProperty("region")
    public String getRegion() { return region; }
    @JsonProperty("region")
    public void setRegion(String value) { this.region = toUTF8(value); }

    @JsonProperty("title")
    public String getTitle() { return title; }
    @JsonProperty("title")
    public void setTitle(String value) { this.title = toUTF8(value); }

    @JsonProperty("leaderboard_url")
    public String getLeaderboardURL() { return leaderboardURL; }
    @JsonProperty("leaderboard_url")
    public void setLeaderboardURL(String value) { this.leaderboardURL = toUTF8(value); }

    @JsonProperty("affix_details")
    public AffixDetail[] getAffixDetails() { return affixDetails; }
    @JsonProperty("affix_details")
    public void setAffixDetails(AffixDetail[] value) { this.affixDetails = value; }

    private String toUTF8(String value) {
        return new String(value.getBytes(), StandardCharsets.UTF_8);
    }
}
