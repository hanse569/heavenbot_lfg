package hv_lfg.youtubeApi;

import java.util.*;
import com.fasterxml.jackson.annotation.*;

public class Default {
    private String url;
    private long width;
    private long height;

    @JsonProperty("url")
    public String geturl() { return url; }
    @JsonProperty("url")
    public void seturl(String value) { this.url = value; }

    @JsonProperty("width")
    public long getWidth() { return width; }
    @JsonProperty("width")
    public void setWidth(long value) { this.width = value; }

    @JsonProperty("height")
    public long getHeight() { return height; }
    @JsonProperty("height")
    public void setHeight(long value) { this.height = value; }
}
