package be.isservers.hmb.youtubeApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YoutubeMusic {
    private String kind;
    private String etag;
    private String nextPageToken;
    private String regionCode;
    private PageInfo pageInfo;
    private List<Item> items;

    @JsonProperty("kind")
    public String getKind() { return kind; }
    @JsonProperty("kind")
    public void setKind(String value) { this.kind = value; }

    @JsonProperty("etag")
    public String getEtag() { return etag; }
    @JsonProperty("etag")
    public void setEtag(String value) { this.etag = value; }

    @JsonProperty("nextPageToken")
    public String getNextPageToken() { return nextPageToken; }
    @JsonProperty("nextPageToken")
    public void setNextPageToken(String value) { this.nextPageToken = value; }

    @JsonProperty("regionCode")
    public String getRegionCode() { return regionCode; }
    @JsonProperty("regionCode")
    public void setRegionCode(String value) { this.regionCode = value; }

    @JsonProperty("pageInfo")
    public PageInfo getPageInfo() { return pageInfo; }
    @JsonProperty("pageInfo")
    public void setPageInfo(PageInfo value) { this.pageInfo = value; }

    @JsonProperty("items")
    public List<Item> getItems() { return items; }
    @JsonProperty("items")
    public void setItems(List<Item> value) { this.items = value; }

    public String getVideoId(){
        return this.getItems().get(0).getid().getVideoId();
    }

    public String getVideoName(){
        return this.getItems().get(0).getSnippet().getTitle();
    }

    public String getMiniature(){
        return this.getItems().get(0).getSnippet().getThumbnails().getThumbnailsDefault().geturl();
    }
}