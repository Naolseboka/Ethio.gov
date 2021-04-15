package et.gov.ethgov.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicy;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyCreated;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyNumber;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ResiliencePolicyTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void createsActivatesAndRetires() {
        ResiliencePolicy policy = ResiliencePolicy.create(
                TenantId.generate(),
                ResiliencePolicyNumber.allocate(2024, 1),
                "ethgov-payments",
                50,
                30,
                5,
                now);
        assertThat(policy.status()).isEqualTo(ResiliencePolicyStatus.DRAFT);
        assertThat(policy.pullDomainEvents()).anyMatch(ResiliencePolicyCreated.class::isInstance);

        policy.activate(now.plusSeconds(5));
        policy.update(40, 45, 3, now.plusSeconds(10));
        assertThat(policy.failureRateThreshold()).isEqualTo(40);

        policy.suspend("dependency degraded", now.plusSeconds(15));
        policy.activate(now.plusSeconds(20));
        policy.retire(now.plusSeconds(25));
        assertThat(policy.status()).isEqualTo(ResiliencePolicyStatus.RETIRED);
        assertThatThrownBy(() -> policy.cancel("too late for cancel", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }
}
