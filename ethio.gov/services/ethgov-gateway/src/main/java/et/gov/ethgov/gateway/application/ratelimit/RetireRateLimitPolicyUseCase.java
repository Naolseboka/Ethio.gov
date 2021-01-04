package et.gov.ethgov.gateway.application.ratelimit;

import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyId;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireRateLimitPolicyUseCase {
    private final RateLimitPolicyRepository policies;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public RetireRateLimitPolicyUseCase(
            RateLimitPolicyRepository policies, GatewayEventPublisher eventPublisher, Clock clock) {
        this.policies = policies;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String rateLimitPolicyId) {
        Objects.requireNonNull(rateLimitPolicyId, "rateLimitPolicyId");
        RateLimitPolicy policy = policies
                .findById(RateLimitPolicyId.of(rateLimitPolicyId))
                .orElseThrow(() -> new DomainException(
                        "RATE_LIMIT_NOT_FOUND", "Rate Limit Not Found", "Unknown policy " + rateLimitPolicyId, 404));
        policy.retire(clock.instant());
        policies.save(policy);
        eventPublisher.publishRateLimitEvents(policy.pullDomainEvents());
    }
}
