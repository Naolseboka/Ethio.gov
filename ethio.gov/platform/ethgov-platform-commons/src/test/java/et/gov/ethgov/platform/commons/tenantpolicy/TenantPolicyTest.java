package et.gov.ethgov.platform.commons.tenantpolicy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TenantPolicyTest {
    @Test
    void beginAndComplete() {
        TenantPolicy subject = new TenantPolicy();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
