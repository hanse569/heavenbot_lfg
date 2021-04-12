package be.isservers.hmb.jsonExtract.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ID {
    private String kind;
    private String videoId;

    @JsonProperty("kind")
    public String getKind() { return kind; }
    @JsonProperty("kind")
    public void setKind(String value) { this.kind = value; }

    @JsonProperty("videoId")
    public String getVideoId() { return videoId; }
    @JsonProperty("videoId")
    public void setVideoId(String value) { this.videoId = value; }
}