package et.gov.ethgov.case.application.caseassignment;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentId;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCaseassignmentUseCase {
    private final CaseassignmentRepository repository;
    private final Clock clock;

    public CreateCaseassignmentUseCase(CaseassignmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CaseassignmentId execute(CreateCaseassignmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Caseassignment aggregate = Caseassignment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCaseassignmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String caseassignmentId,
            String reason) {}
}
