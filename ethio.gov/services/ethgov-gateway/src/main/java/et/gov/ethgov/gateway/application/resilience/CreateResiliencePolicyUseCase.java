package et.gov.ethgov.gateway.application.resilience;

import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicy;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyId;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyNumber;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyNumberSequence;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicyRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateResiliencePolicyUseCase {
    private final ResiliencePolicyRepository policies;
    private final ResiliencePolicyNumberSequence sequences;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public CreateResiliencePolicyUseCase(
            ResiliencePolicyRepository policies,
            ResiliencePolicyNumberSequence sequences,
            GatewayEventPublisher eventPublisher,
            Clock clock) {
        this.policies = policies;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ResiliencePolicyId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        String dependency = command.dependencyName().trim().toLowerCase(Locale.ROOT);
        if (policies.existsByTenantAndDependency(tenantId, dependency)) {
            throw new DomainException(
                    "RESILIENCE_DEPENDENCY_EXISTS",
                    "Resilience Dependency Exists",
                    "A resilience policy already exists for this dependency",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        ResiliencePolicyNumber number =
                ResiliencePolicyNumber.allocate(year, sequences.nextSequence(tenantId, year));
        ResiliencePolicy policy = ResiliencePolicy.create(
                tenantId,
                number,
                dependency,
                command.failureRateThreshold(),
                command.waitDurationSeconds(),
                command.permittedCallsInHalfOpen(),
                clock.instant());
        policies.save(policy);
        eventPublisher.publishResilienceEvents(policy.pullDomainEvents());
        return policy.id();
    }

    public record Command(
            String tenantId,
            String dependencyName,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen) {}
}
