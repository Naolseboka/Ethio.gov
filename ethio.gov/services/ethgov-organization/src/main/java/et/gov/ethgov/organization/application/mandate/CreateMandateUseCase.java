package et.gov.ethgov.organization.application.mandate;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateId;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMandateUseCase {
    private final MandateRepository repository;
    private final Clock clock;

    public CreateMandateUseCase(MandateRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MandateId execute(CreateMandateCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Mandate aggregate = Mandate.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMandateCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mandateId,
            String reason) {}
}
