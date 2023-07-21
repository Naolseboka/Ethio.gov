package et.gov.ethgov.organization.application.mandate;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateId;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateMandateUseCase {
    private final MandateRepository repository;
    private final Clock clock;

    public UpdateMandateUseCase(MandateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MandateId execute(UpdateMandateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MandateId id = MandateId.parse(command.mandateId());
        Mandate aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mandate not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateMandateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mandateId,
            String reason) {}
}
