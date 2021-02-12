package be.isservers.hmb.jsonExtract.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {
    private String kind;
    private String etag;
    private ID id;
    private Snippet snippet;

    @JsonProperty("kind")
    public String getKind() { return kind; }
    @JsonProperty("kind")
    public void setKind(String value) { this.kind = value; }

    @JsonProperty("etag")
    public String getEtag() { return etag; }
    @JsonProperty("etag")
    public void setEtag(String value) { this.etag = value; }

    @JsonProperty("id")
    public ID getid() { return id; }
    @JsonProperty("id")
    public void setid(ID value) { this.id = value; }

    @JsonProperty("snippet")
    public Snippet getSnippet() { return snippet; }
    @JsonProperty("snippet")
    public void setSnippet(Snippet value) { this.snippet = value; }
}