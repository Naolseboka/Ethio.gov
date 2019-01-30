package et.gov.ethgov.case.application.casenote;

import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteId;
import et.gov.ethgov.case.domain.casenote.CasenoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCasenoteUseCase {
    private final CasenoteRepository repository;
    private final Clock clock;

    public SuspendCasenoteUseCase(CasenoteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CasenoteId execute(SuspendCasenoteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CasenoteId id = CasenoteId.parse(command.casenoteId());
        Casenote aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("casenote not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCasenoteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String casenoteId,
            String reason) {}
}
