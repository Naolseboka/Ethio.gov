package et.gov.ethgov.platform.messaging.retry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RetryPolicyTest {
    @Test
    void beginAndComplete() {
        RetryPolicy subject = new RetryPolicy();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
