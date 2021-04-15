package et.gov.ethgov.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.gateway.domain.audit.AuditEntry;
import et.gov.ethgov.gateway.domain.audit.AuditEntryAppended;
import et.gov.ethgov.gateway.domain.audit.AuditOutcome;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AuditEntryTest {

    @Test
    void appendsImmutableEntry() {
        Instant now = Instant.parse("2024-06-01T00:00:00Z");
        AuditEntry entry = AuditEntry.append(
                TenantId.generate(),
                "usr_ops",
                "policy.activate",
                "rate_limit_policy",
                "rlp_11111111-1111-4111-8111-111111111111",
                AuditOutcome.SUCCESS,
                "{\"source\":\"ops\"}",
                now);
        assertThat(entry.action()).isEqualTo("policy.activate");
        assertThat(entry.pullDomainEvents()).anyMatch(AuditEntryAppended.class::isInstance);
    }
}
