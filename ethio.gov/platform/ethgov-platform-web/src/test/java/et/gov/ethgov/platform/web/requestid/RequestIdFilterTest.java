package et.gov.ethgov.platform.web.requestid;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RequestIdFilterTest {
    @Test
    void beginAndComplete() {
        RequestIdFilter subject = new RequestIdFilter();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
