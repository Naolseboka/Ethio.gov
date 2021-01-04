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
public class UpdateRateLimitPolicyUseCase {
    private final RateLimitPolicyRepository policies;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateRateLimitPolicyUseCase(
            RateLimitPolicyRepository policies, GatewayEventPublisher eventPublisher, Clock clock) {
        this.policies = policies;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        RateLimitPolicy policy = policies
                .findById(RateLimitPolicyId.of(command.rateLimitPolicyId()))
                .orElseThrow(() -> notFound(command.rateLimitPolicyId()));
        policy.update(command.name(), command.limitPerWindow(), command.windowSeconds(), clock.instant());
        policies.save(policy);
        eventPublisher.publishRateLimitEvents(policy.pullDomainEvents());
    }

    private static DomainException notFound(String id) {
        return new DomainException("RATE_LIMIT_NOT_FOUND", "Rate Limit Not Found", "Unknown policy " + id, 404);
    }

    public record Command(String rateLimitPolicyId, String name, int limitPerWindow, int windowSeconds) {}
}
