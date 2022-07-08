package et.gov.ethgov.licensing.application.renewal;

import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalId;
import et.gov.ethgov.licensing.domain.renewal.RenewalRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRenewalUseCase {
    private final RenewalRepository repository;
    private final Clock clock;

    public SuspendRenewalUseCase(RenewalRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RenewalId execute(SuspendRenewalCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RenewalId id = RenewalId.parse(command.renewalId());
        Renewal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("renewal not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRenewalCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String renewalId,
            String reason) {}
}
