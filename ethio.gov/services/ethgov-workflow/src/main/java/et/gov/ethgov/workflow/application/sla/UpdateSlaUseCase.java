package et.gov.ethgov.workflow.application.sla;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaId;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSlaUseCase {
    private final SlaRepository repository;
    private final Clock clock;

    public UpdateSlaUseCase(SlaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SlaId execute(UpdateSlaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SlaId id = SlaId.parse(command.slaId());
        Sla aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sla not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSlaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String slaId,
            String reason) {}
}
