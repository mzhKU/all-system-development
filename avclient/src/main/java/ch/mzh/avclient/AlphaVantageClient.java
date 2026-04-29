package ch.mzh.avclient;


import ch.mzh.avclient.domain.DailyTimeSeries;
import ch.mzh.avclient.domain.MetaData;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;

@SpringBootApplication
@EnableScheduling
public class AlphaVantageClient {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);

    // This matches the name defined in the Kubernetes Deployment 'env' section
    @Value("${AV_CLIENT_SECRET:invalid_key}")
    private String apiKey;

    @Value("${realRequest}")
    private Boolean realRequest;

    public static void main(String[] args) {
        SpringApplication.run(AlphaVantageClient.class, args);
    }

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        logger.info("Pod Heartbeat: AlphaVantage Client is alive at {}", LocalDateTime.now());
    }
}
