package et.gov.ethgov.platform.messaging.deadletter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DeadLetterTest {
    @Test
    void beginAndComplete() {
        DeadLetter subject = new DeadLetter();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
