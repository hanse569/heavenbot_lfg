package be.isservers.hmb.jsonExtract.wowtokenprices;

import com.fasterxml.jackson.annotation.*;
import java.time.OffsetDateTime;

public class Region {
    private String region;
    private long currentPrice;
    private long lastChange;
    private OffsetDateTime timeOfLastChangeUTCTimezone;
    private long timeOfLastChangeUnixEpoch;
    private long the1_DayLow;
    private long the1_DayHigh;
    private long the7_DayLow;
    private long the7_DayHigh;
    private long the30_DayLow;
    private long the30_DayHigh;
    private String isFromAPI;

    @JsonProperty("region")
    public String getRegion() { return region; }
    @JsonProperty("region")
    public void setRegion(String value) { this.region = value; }

    @JsonProperty("current_price")
    public long getCurrentPrice() { return currentPrice; }
    @JsonProperty("current_price")
    public void setCurrentPrice(long value) { this.currentPrice = value; }

    @JsonProperty("last_change")
    public long getLastChange() { return lastChange; }
    @JsonProperty("last_change")
    public void setLastChange(long value) { this.lastChange = value; }

    @JsonProperty("time_of_last_change_utc_timezone")
    public OffsetDateTime getTimeOfLastChangeUTCTimezone() { return timeOfLastChangeUTCTimezone; }
    @JsonProperty("time_of_last_change_utc_timezone")
    public void setTimeOfLastChangeUTCTimezone(OffsetDateTime value) { this.timeOfLastChangeUTCTimezone = value; }

    @JsonProperty("time_of_last_change_unix_epoch")
    public long getTimeOfLastChangeUnixEpoch() { return timeOfLastChangeUnixEpoch; }
    @JsonProperty("time_of_last_change_unix_epoch")
    public void setTimeOfLastChangeUnixEpoch(long value) { this.timeOfLastChangeUnixEpoch = value; }

    @JsonProperty("1_day_low")
    public long getThe1DayLow() { return the1_DayLow; }
    @JsonProperty("1_day_low")
    public void setThe1DayLow(long value) { this.the1_DayLow = value; }

    @JsonProperty("1_day_high")
    public long getThe1DayHigh() { return the1_DayHigh; }
    @JsonProperty("1_day_high")
    public void setThe1DayHigh(long value) { this.the1_DayHigh = value; }

    @JsonProperty("7_day_low")
    public long getThe7DayLow() { return the7_DayLow; }
    @JsonProperty("7_day_low")
    public void setThe7DayLow(long value) { this.the7_DayLow = value; }

    @JsonProperty("7_day_high")
    public long getThe7DayHigh() { return the7_DayHigh; }
    @JsonProperty("7_day_high")
    public void setThe7DayHigh(long value) { this.the7_DayHigh = value; }

    @JsonProperty("30_day_low")
    public long getThe30DayLow() { return the30_DayLow; }
    @JsonProperty("30_day_low")
    public void setThe30DayLow(long value) { this.the30_DayLow = value; }

    @JsonProperty("30_day_high")
    public long getThe30DayHigh() { return the30_DayHigh; }
    @JsonProperty("30_day_high")
    public void setThe30DayHigh(long value) { this.the30_DayHigh = value; }

    @JsonProperty("is_from_api")
    public String getIsFromAPI() { return isFromAPI; }
    @JsonProperty("is_from_api")
    public void setIsFromAPI(String value) { this.isFromAPI = value; }
}

