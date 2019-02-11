package et.gov.ethgov.case.application.evidence;

import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceId;
import et.gov.ethgov.case.domain.evidence.EvidenceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendEvidenceUseCase {
    private final EvidenceRepository repository;
    private final Clock clock;

    public SuspendEvidenceUseCase(EvidenceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EvidenceId execute(SuspendEvidenceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EvidenceId id = EvidenceId.parse(command.evidenceId());
        Evidence aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("evidence not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendEvidenceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String evidenceId,
            String reason) {}
}
