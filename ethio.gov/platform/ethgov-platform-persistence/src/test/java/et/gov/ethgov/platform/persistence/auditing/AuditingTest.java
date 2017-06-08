package et.gov.ethgov.platform.persistence.auditing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AuditingTest {
    @Test
    void beginAndComplete() {
        Auditing subject = new Auditing();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
