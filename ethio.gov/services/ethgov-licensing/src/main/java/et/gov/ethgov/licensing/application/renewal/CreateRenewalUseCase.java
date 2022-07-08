package et.gov.ethgov.licensing.application.renewal;

import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalId;
import et.gov.ethgov.licensing.domain.renewal.RenewalRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRenewalUseCase {
    private final RenewalRepository repository;
    private final Clock clock;

    public CreateRenewalUseCase(RenewalRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RenewalId execute(CreateRenewalCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Renewal aggregate = Renewal.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRenewalCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String renewalId,
            String reason) {}
}
