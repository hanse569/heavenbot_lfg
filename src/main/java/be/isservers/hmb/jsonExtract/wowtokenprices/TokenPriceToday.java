package be.isservers.hmb.jsonExtract.wowtokenprices;

import com.fasterxml.jackson.annotation.*;

public class TokenPriceToday {
    private Region us;
    private Region eu;
    private Region china;
    private Region korea;
    private Region taiwan;

    @JsonProperty("us")
    public Region getUs() { return us; }
    @JsonProperty("us")
    public void setUs(Region value) { this.us = value; }

    @JsonProperty("eu")
    public Region getEu() { return eu; }
    @JsonProperty("eu")
    public void setEu(Region value) { this.eu = value; }

    @JsonProperty("china")
    public Region getChina() { return china; }
    @JsonProperty("china")
    public void setChina(Region value) { this.china = value; }

    @JsonProperty("korea")
    public Region getKorea() { return korea; }
    @JsonProperty("korea")
    public void setKorea(Region value) { this.korea = value; }

    @JsonProperty("taiwan")
    public Region getTaiwan() { return taiwan; }
    @JsonProperty("taiwan")
    public void setTaiwan(Region value) { this.taiwan = value; }
}
