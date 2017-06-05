package et.gov.ethgov.platform.observability.metrics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DomainMetricsTest {
    @Test
    void beginAndComplete() {
        DomainMetrics subject = new DomainMetrics();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
