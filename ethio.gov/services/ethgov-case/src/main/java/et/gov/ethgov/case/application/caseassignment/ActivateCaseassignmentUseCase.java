package et.gov.ethgov.case.application.caseassignment;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentId;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCaseassignmentUseCase {
    private final CaseassignmentRepository repository;
    private final Clock clock;

    public ActivateCaseassignmentUseCase(CaseassignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CaseassignmentId execute(ActivateCaseassignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CaseassignmentId id = CaseassignmentId.parse(command.caseassignmentId());
        Caseassignment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("caseassignment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCaseassignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String caseassignmentId,
            String reason) {}
}
