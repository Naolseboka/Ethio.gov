package et.gov.ethgov.platform.observability.tracing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpanEnrichmentTest {
    @Test
    void beginAndComplete() {
        SpanEnrichment subject = new SpanEnrichment();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
