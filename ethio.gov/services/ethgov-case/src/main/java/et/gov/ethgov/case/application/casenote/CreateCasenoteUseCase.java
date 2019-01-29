package et.gov.ethgov.case.application.casenote;

import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteId;
import et.gov.ethgov.case.domain.casenote.CasenoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCasenoteUseCase {
    private final CasenoteRepository repository;
    private final Clock clock;

    public CreateCasenoteUseCase(CasenoteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CasenoteId execute(CreateCasenoteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Casenote aggregate = Casenote.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCasenoteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String casenoteId,
            String reason) {}
}
