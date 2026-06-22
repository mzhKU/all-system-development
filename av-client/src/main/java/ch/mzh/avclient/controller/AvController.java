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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class AvController {
    private volatile boolean highTrafficSimulated = false;
    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);
    private static String AV_BASE_URL = "https://www.alphavantage.co/";

    // This matches the name defined in the Kubernetes Deployment 'env' section
    @Value("${AV_CLIENT_SECRET:invalid_key}")
    private String apiKey;

    @Value("${realRequest}")
    private Boolean realRequest;

    @GetMapping("/test-error")
    public void error() {
        // comment
        throw new RuntimeException("HTTP 500");
    }

    @GetMapping("/load")
    public String toggleLoad(@RequestParam(value = "traffic", defaultValue = "low") String traffic) {
        if ("high".equalsIgnoreCase(traffic)) {
            if (highTrafficSimulated) {
                return "CPU load simulation is already running.";
            }
            
            highTrafficSimulated = true;
            // Run the heavy CPU load in an asynchronous background thread
            CompletableFuture.runAsync(this::burnCPU);
            return "High traffic simulation started. Driving CPU up...";
        } else {
            highTrafficSimulated = false;
            return "Traffic simulation reset to low.";
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyTimeSeries> getDailyTimeSeries() {
        logger.info("Starting API request with key: {}", apiKey);

        RestClient restClient = RestClient.builder().baseUrl(AV_BASE_URL).build();

        try {
            DailyTimeSeries dailyTimeSeries;
            if (realRequest) {
                dailyTimeSeries = restClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/query")
                                .queryParam("function", "TIME_SERIES_DAILY")
                                .queryParam("symbol", "IBM")
                                .queryParam("apikey", apiKey)
                                .build())
                        .retrieve()
                        .body(DailyTimeSeries.class);
            } else {
                logger.info("USING MOCK TIMESERIES");
                LocalDate queryDate = LocalDate.of(2026, 1, 1);
                OpenHighLowCloseVolume mockResponse = new OpenHighLowCloseVolume(1.0, 2.0, 1.4, 1.2, 10);
                MetaData mockMetaData = new MetaData("Info","SYMB",queryDate,"Output Size","CET");
                dailyTimeSeries = new DailyTimeSeries(mockMetaData, Map.of(queryDate, mockResponse));
            }

            if (dailyTimeSeries != null && dailyTimeSeries.dailyTimeSeries() != null) {
                for (Map.Entry<LocalDate, OpenHighLowCloseVolume> entry : dailyTimeSeries.dailyTimeSeries().entrySet()) {
                    logger.info("Date: {} - High: {}", entry.getKey(), entry.getValue().high());
                }
            } else {
                logger.warn("Received empty response from AlphaVantage API.");
            }
            return ResponseEntity.ok(dailyTimeSeries);
        } catch (Exception e) {
            logger.error("Failed to fetch data from API: {}", e.getMessage());
        }

        logger.info("AlphaVantage Client initialization logic complete.");
        // some change
        return ResponseEntity.ok(new DailyTimeSeries(null, null));
    }

    private void burnCPU() {
        // Run continuous operations while the flag remains true
        while (highTrafficSimulated) {
            // Generating random UUIDs and hashing them keeps the CPU intensively busy
            String stringToHash = UUID.randomUUID().toString();
            int hashCode = stringToHash.hashCode();
            
            // Minor safety break to prevent thread lockup, but keeps CPU usage near maximum
            if (hashCode == 0) {
                System.out.println("Collision found");
            }
        }
        System.out.println("Traffic simulation stopped. Thread cooling down.");
    }

}
