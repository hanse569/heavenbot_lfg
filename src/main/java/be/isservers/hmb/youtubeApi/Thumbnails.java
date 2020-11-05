package be.isservers.hmb.youtubeApi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Thumbnails {
    private Default thumbnailsDefault;
    private Default medium;
    private Default high;

    @JsonProperty("default")
    public Default getThumbnailsDefault() { return thumbnailsDefault; }
    @JsonProperty("default")
    public void setThumbnailsDefault(Default value) { this.thumbnailsDefault = value; }

    @JsonProperty("medium")
    public Default getMedium() { return medium; }
    @JsonProperty("medium")
    public void setMedium(Default value) { this.medium = value; }

    @JsonProperty("high")
    public Default getHigh() { return high; }
    @JsonProperty("high")
    public void setHigh(Default value) { this.high = value; }
}