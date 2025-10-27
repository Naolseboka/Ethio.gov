package et.gov.ethgov.workflow.application.sla;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaId;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSlaUseCase {
    private final SlaRepository repository;
    private final Clock clock;

    public CreateSlaUseCase(SlaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SlaId execute(CreateSlaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Sla aggregate = Sla.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSlaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String slaId,
            String reason) {}
}
