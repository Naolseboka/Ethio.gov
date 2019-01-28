package et.gov.ethgov.case.application.casefile;

import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileId;
import et.gov.ethgov.case.domain.casefile.CasefileRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCasefileUseCase {
    private final CasefileRepository repository;
    private final Clock clock;

    public CreateCasefileUseCase(CasefileRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CasefileId execute(CreateCasefileCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Casefile aggregate = Casefile.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCasefileCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String casefileId,
            String reason) {}
}
