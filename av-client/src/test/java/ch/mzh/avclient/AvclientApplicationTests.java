package ch.mzh.avclient;

import ch.mzh.avclient.domain.DailyTimeSeries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@SpringBootTest
class AvclientApplicationTests {

	private static RestClient restClient;

	// This matches the name defined in the Kubernetes Deployment 'env' section
	@Value("${AV_CLIENT_SECRET:invalid_key}")
	private String apiKey;

	@BeforeAll
	public static void setup() {
		RestClient.Builder restClientBuilder = RestClient.builder();
		restClient = restClientBuilder.baseUrl("https://www.alphavantage.co/").build();
	}

	@Test
	void contextLoads() {
		DailyTimeSeries dailyTimeSeries = restClient
				.get()
				.uri("/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=" + apiKey)
				.retrieve()
				.body(DailyTimeSeries.class);
		assert dailyTimeSeries != null;

	}

}
