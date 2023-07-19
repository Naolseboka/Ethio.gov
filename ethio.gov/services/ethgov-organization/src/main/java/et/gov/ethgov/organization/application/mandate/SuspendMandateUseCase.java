package et.gov.ethgov.organization.application.mandate;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateId;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendMandateUseCase {
    private final MandateRepository repository;
    private final Clock clock;

    public SuspendMandateUseCase(MandateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MandateId execute(SuspendMandateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MandateId id = MandateId.parse(command.mandateId());
        Mandate aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mandate not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendMandateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mandateId,
            String reason) {}
}
