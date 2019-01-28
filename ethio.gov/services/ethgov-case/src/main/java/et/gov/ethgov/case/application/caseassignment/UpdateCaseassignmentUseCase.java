package et.gov.ethgov.case.application.caseassignment;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentId;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCaseassignmentUseCase {
    private final CaseassignmentRepository repository;
    private final Clock clock;

    public UpdateCaseassignmentUseCase(CaseassignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CaseassignmentId execute(UpdateCaseassignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CaseassignmentId id = CaseassignmentId.parse(command.caseassignmentId());
        Caseassignment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("caseassignment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCaseassignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String caseassignmentId,
            String reason) {}
}
