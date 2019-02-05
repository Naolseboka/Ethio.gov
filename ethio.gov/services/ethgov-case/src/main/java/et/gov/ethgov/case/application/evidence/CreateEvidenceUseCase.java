package et.gov.ethgov.case.application.evidence;

import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceId;
import et.gov.ethgov.case.domain.evidence.EvidenceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEvidenceUseCase {
    private final EvidenceRepository repository;
    private final Clock clock;

    public CreateEvidenceUseCase(EvidenceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EvidenceId execute(CreateEvidenceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Evidence aggregate = Evidence.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEvidenceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String evidenceId,
            String reason) {}
}
