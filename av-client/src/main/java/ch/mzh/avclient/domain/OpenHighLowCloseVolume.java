package ch.mzh.avclient.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenHighLowCloseVolume(
        @JsonProperty("1. open") double open,
        @JsonProperty("2. high") double high,
        @JsonProperty("3. low") double low,
        @JsonProperty("4. close") double close,
        @JsonProperty("5. volume") int volume) {


}
