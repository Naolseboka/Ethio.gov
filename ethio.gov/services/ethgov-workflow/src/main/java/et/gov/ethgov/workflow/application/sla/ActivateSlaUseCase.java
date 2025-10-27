package et.gov.ethgov.workflow.application.sla;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaId;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSlaUseCase {
    private final SlaRepository repository;
    private final Clock clock;

    public ActivateSlaUseCase(SlaRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SlaId execute(ActivateSlaCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SlaId id = SlaId.parse(command.slaId());
        Sla aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sla not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSlaCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String slaId,
            String reason) {}
}
