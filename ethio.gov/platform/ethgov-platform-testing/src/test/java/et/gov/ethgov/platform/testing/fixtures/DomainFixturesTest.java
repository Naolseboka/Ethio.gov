package et.gov.ethgov.platform.testing.fixtures;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DomainFixturesTest {
    @Test
    void beginAndComplete() {
        DomainFixtures subject = new DomainFixtures();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
