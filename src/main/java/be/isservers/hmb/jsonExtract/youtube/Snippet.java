package be.isservers.hmb.jsonExtract.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Snippet {
    private String publishedAt;
    private String channelId;
    private String title;
    private String description;
    private Thumbnails thumbnails;
    private String channelTitle;
    private String liveBroadcastContent;
    private String publishTime;

    @JsonProperty("publishedAt")
    public String getPublishedAt() { return publishedAt; }
    @JsonProperty("publishedAt")
    public void setPublishedAt(String value) { this.publishedAt = value; }

    @JsonProperty("channelId")
    public String getChannelId() { return channelId; }
    @JsonProperty("channelId")
    public void setChannelId(String value) { this.channelId = value; }

    @JsonProperty("title")
    public String getTitle() { return title; }
    @JsonProperty("title")
    public void setTitle(String value) { this.title = value; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    @JsonProperty("description")
    public void setDescription(String value) { this.description = value; }

    @JsonProperty("thumbnails")
    public Thumbnails getThumbnails() { return thumbnails; }
    @JsonProperty("thumbnails")
    public void setThumbnails(Thumbnails value) { this.thumbnails = value; }

    @JsonProperty("channelTitle")
    public String getChannelTitle() { return channelTitle; }
    @JsonProperty("channelTitle")
    public void setChannelTitle(String value) { this.channelTitle = value; }

    @JsonProperty("liveBroadcastContent")
    public String getLiveBroadcastContent() { return liveBroadcastContent; }
    @JsonProperty("liveBroadcastContent")
    public void setLiveBroadcastContent(String value) { this.liveBroadcastContent = value; }

    @JsonProperty("publishTime")
    public String getPublishTime() { return publishTime; }
    @JsonProperty("publishTime")
    public void setPublishTime(String value) { this.publishTime = value; }
}