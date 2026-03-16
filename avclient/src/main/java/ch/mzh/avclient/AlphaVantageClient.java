package ch.mzh.avclient;


import ch.mzh.avclient.domain.DailyTimeSeries;
import ch.mzh.avclient.domain.OpenHighLowCloseVolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map.Entry;

@SpringBootApplication
@EnableScheduling
public class AlphaVantageClient implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);

    // This matches the name defined in the Kubernetes Deployment 'env' section
    @Value("${AV_CLIENT_SECRET:invalid_key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(AlphaVantageClient.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("Starting API request with key: {}", apiKey);

        RestClient restClient = RestClient.builder()
                .baseUrl("https://www.alphavantage.co/")
                .build();

        try {
            DailyTimeSeries dailyTimeSeries = restClient
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/query")
                                    .queryParam("function", "TIME_SERIES_DAILY")
                                    .queryParam("symbol", "IBM")
                                    .queryParam("apikey", apiKey)
                                    .build())
                    .retrieve()
                    .body(DailyTimeSeries.class);

            if (dailyTimeSeries != null && dailyTimeSeries.dailyTimeSeries() != null) {
                for (Entry<java.time.LocalDate, OpenHighLowCloseVolume> entry : dailyTimeSeries.dailyTimeSeries().entrySet()) {
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

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        logger.info("Pod Heartbeat: AlphaVantage Client is alive at {}", LocalDateTime.now());
    }
}
