package et.gov.ethgov.platform.security.scope;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ScopeGuardTest {
    @Test
    void beginAndComplete() {
        ScopeGuard subject = new ScopeGuard();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
