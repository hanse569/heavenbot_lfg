package be.isservers.hmb.youtubeApi;

import com.fasterxml.jackson.annotation.*;

public class PageInfo {
    private long totalResults;
    private long resultsPerPage;

    @JsonProperty("totalResults")
    public long getTotalResults() { return totalResults; }
    @JsonProperty("totalResults")
    public void setTotalResults(long value) { this.totalResults = value; }

    @JsonProperty("resultsPerPage")
    public long getResultsPerPage() { return resultsPerPage; }
    @JsonProperty("resultsPerPage")
    public void setResultsPerPage(long value) { this.resultsPerPage = value; }
}