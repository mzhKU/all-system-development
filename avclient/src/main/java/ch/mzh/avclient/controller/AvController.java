package ch.mzh.avclient.controller;

import ch.mzh.avclient.AlphaVantageClient;
import ch.mzh.avclient.domain.DailyTimeSeries;
import ch.mzh.avclient.domain.MetaData;
import ch.mzh.avclient.domain.OpenHighLowCloseVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class AvController {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);

    // This matches the name defined in the Kubernetes Deployment 'env' section
    @Value("${AV_CLIENT_SECRET:invalid_key}")
    private String apiKey;

    @Value("${realRequest}")
    private Boolean realRequest;

    @GetMapping("/daily")
    public void getDailyTimeSeries() {
        logger.info("Starting API request with key: {}", apiKey);

        RestClient restClient = RestClient.builder().baseUrl("https://www.alphavantage.co/").build();

        try {
            DailyTimeSeries dailyTimeSeries;
            if (realRequest) {
                dailyTimeSeries = restClient.get().uri(uriBuilder -> uriBuilder.path("/query").queryParam("function", "TIME_SERIES_DAILY").queryParam("symbol", "IBM").queryParam("apikey", apiKey).build()).retrieve().body(DailyTimeSeries.class);
            } else {
                logger.info("USING MOCK TIMESERIES");
                dailyTimeSeries = new DailyTimeSeries(new MetaData("Info", "SYMB", LocalDate.of(2026, 1, 1), "Output Size", "CET"), Map.of(LocalDate.of(2026, 1, 1), new OpenHighLowCloseVolume(1.0, 2.0, 1.4, 1.2, 10)));
            }

            if (dailyTimeSeries != null && dailyTimeSeries.dailyTimeSeries() != null) {
                for (Map.Entry<LocalDate, OpenHighLowCloseVolume> entry : dailyTimeSeries.dailyTimeSeries().entrySet()) {
                    logger.info("Date: {} - High: {}", entry.getKey(), entry.getValue().high());
                }
            } else {
                logger.warn("Received empty response from AlphaVantage API.");
            }
        } catch (Exception e) {
            logger.error("Failed to fetch data from API: {}", e.getMessage());
        }

        logger.info("AlphaVantage Client initialization logic complete.");
    }
}
