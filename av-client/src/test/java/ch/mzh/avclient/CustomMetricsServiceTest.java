package ch.mzh.avclient;

import ch.mzh.avclient.monitor.CustomMetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class CustomMetricsServiceTest {

    @Test
    void counterIncrementsOnCall() {
        MeterRegistry registry = new SimpleMeterRegistry();
        CustomMetricsService service = new CustomMetricsService(registry);

        service.incrementCustomMetric();

        Counter counter = registry.find("custom_metric_name").counter();
        assert counter != null;
        assert counter.count() == 1.0;
    }
}