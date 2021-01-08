package et.gov.ethgov.gateway.application.resilience;

import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicy;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyId;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateResiliencePolicyUseCase {
    private final ResiliencePolicyRepository policies;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateResiliencePolicyUseCase(
            ResiliencePolicyRepository policies, GatewayEventPublisher eventPublisher, Clock clock) {
        this.policies = policies;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        ResiliencePolicy policy = policies
                .findById(ResiliencePolicyId.of(command.resiliencePolicyId()))
                .orElseThrow(() -> new DomainException(
                        "RESILIENCE_NOT_FOUND",
                        "Resilience Not Found",
                        "Unknown policy " + command.resiliencePolicyId(),
                        404));
        policy.update(
                command.failureRateThreshold(),
                command.waitDurationSeconds(),
                command.permittedCallsInHalfOpen(),
                clock.instant());
        policies.save(policy);
        eventPublisher.publishResilienceEvents(policy.pullDomainEvents());
    }

    public record Command(
            String resiliencePolicyId,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen) {}
}
