package et.gov.ethgov.gateway.application.ratelimit;

import et.gov.ethgov.gateway.application.GatewayEventPublisher;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyId;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyNumber;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyNumberSequence;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicyRepository;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitSubjectType;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRateLimitPolicyUseCase {
    private final RateLimitPolicyRepository policies;
    private final RateLimitPolicyNumberSequence sequences;
    private final GatewayEventPublisher eventPublisher;
    private final Clock clock;

    public CreateRateLimitPolicyUseCase(
            RateLimitPolicyRepository policies,
            RateLimitPolicyNumberSequence sequences,
            GatewayEventPublisher eventPublisher,
            Clock clock) {
        this.policies = policies;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public RateLimitPolicyId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        RateLimitSubjectType subjectType = RateLimitSubjectType.valueOf(command.subjectType().trim().toUpperCase());
        String subjectKey = command.subjectKey().trim();
        if (subjectType == RateLimitSubjectType.ROUTE) {
            subjectKey = subjectKey.toLowerCase();
        }
        if (policies.existsByTenantAndSubject(tenantId, subjectType, subjectKey)) {
            throw new DomainException(
                    "RATE_LIMIT_SUBJECT_EXISTS",
                    "Rate Limit Subject Exists",
                    "An active or draft rate-limit policy already exists for this subject",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        RateLimitPolicyNumber number =
                RateLimitPolicyNumber.allocate(year, sequences.nextSequence(tenantId, year));
        RateLimitPolicy policy = RateLimitPolicy.create(
                tenantId,
                number,
                command.name(),
                subjectType,
                subjectKey,
                command.limitPerWindow(),
                command.windowSeconds(),
                clock.instant());
        policies.save(policy);
        eventPublisher.publishRateLimitEvents(policy.pullDomainEvents());
        return policy.id();
    }

    public record Command(
            String tenantId,
            String name,
            String subjectType,
            String subjectKey,
            int limitPerWindow,
            int windowSeconds) {}
}
