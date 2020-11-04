package be.isservers.hmb.youtubeApi;

import com.fasterxml.jackson.annotation.*;

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