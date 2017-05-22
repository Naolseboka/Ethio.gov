package et.gov.ethgov.platform.commons.idempotency;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IdempotencyTest {
    @Test
    void beginAndComplete() {
        Idempotency subject = new Idempotency();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
