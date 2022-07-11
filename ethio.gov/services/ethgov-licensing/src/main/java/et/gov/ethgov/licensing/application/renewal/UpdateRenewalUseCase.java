package et.gov.ethgov.licensing.application.renewal;

import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalId;
import et.gov.ethgov.licensing.domain.renewal.RenewalRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRenewalUseCase {
    private final RenewalRepository repository;
    private final Clock clock;

    public UpdateRenewalUseCase(RenewalRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RenewalId execute(UpdateRenewalCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RenewalId id = RenewalId.parse(command.renewalId());
        Renewal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("renewal not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRenewalCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String renewalId,
            String reason) {}
}
