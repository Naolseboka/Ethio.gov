package et.gov.ethgov.case.application.casefile;

import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileId;
import et.gov.ethgov.case.domain.casefile.CasefileRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCasefileUseCase {
    private final CasefileRepository repository;
    private final Clock clock;

    public SuspendCasefileUseCase(CasefileRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CasefileId execute(SuspendCasefileCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CasefileId id = CasefileId.parse(command.casefileId());
        Casefile aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("casefile not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCasefileCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String casefileId,
            String reason) {}
}
