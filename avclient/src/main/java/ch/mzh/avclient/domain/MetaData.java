package ch.mzh.avclient.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaData {
    @JsonProperty("1. Information")
    private String information;

    @JsonProperty("2. Symbol")
    private String symbol;

    @JsonProperty("3. Last Refreshed")
    private LocalDate lastRefreshed;

    @JsonProperty("4. Output Size")
    private String outputSize;

    @JsonProperty("5. Time Zone")
    private String timeZone;
}
