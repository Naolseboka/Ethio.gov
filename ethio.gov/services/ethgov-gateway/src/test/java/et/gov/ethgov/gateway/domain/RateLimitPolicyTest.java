package et.gov.ethgov.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyActivated;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyCreated;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyNumber;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyStatus;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitSubjectType;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class RateLimitPolicyTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void createsActivatesSuspendsAndRetires() {
        RateLimitPolicy policy = RateLimitPolicy.create(
                TenantId.generate(),
                RateLimitPolicyNumber.allocate(2024, 1),
                "Partner app quota",
                RateLimitSubjectType.APP,
                "dap_11111111-1111-4111-8111-111111111111",
                100,
                60,
                now);
        assertThat(policy.status()).isEqualTo(RateLimitPolicyStatus.DRAFT);
        assertThat(policy.pullDomainEvents()).anyMatch(RateLimitPolicyCreated.class::isInstance);

        policy.activate(now.plusSeconds(10));
        assertThat(policy.status()).isEqualTo(RateLimitPolicyStatus.ACTIVE);
        assertThat(policy.pullDomainEvents()).anyMatch(RateLimitPolicyActivated.class::isInstance);

        policy.suspend("abuse investigation", now.plusSeconds(20));
        assertThat(policy.status()).isEqualTo(RateLimitPolicyStatus.SUSPENDED);

        policy.activate(now.plusSeconds(30));
        policy.retire(now.plusSeconds(40));
        assertThat(policy.status()).isEqualTo(RateLimitPolicyStatus.RETIRED);
        assertThatThrownBy(() -> policy.cancel("too late", now.plusSeconds(50)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void alignsWindowStart() {
        RateLimitPolicy policy = RateLimitPolicy.create(
                TenantId.generate(),
                RateLimitPolicyNumber.allocate(2024, 2),
                "Route quota",
                RateLimitSubjectType.ROUTE,
                "/v1/citizens",
                10,
                60,
                now);
        Instant mid = Instant.parse("2024-06-01T00:00:45Z");
        assertThat(policy.windowStart(mid)).isEqualTo(Instant.parse("2024-06-01T00:00:00Z"));
    }

    @Test
    void rejectsInvalidAppSubject() {
        assertThatThrownBy(() -> RateLimitPolicy.create(
                        TenantId.generate(),
                        RateLimitPolicyNumber.allocate(2024, 3),
                        "Bad",
                        RateLimitSubjectType.APP,
                        "not-an-app-id",
                        10,
                        60,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
