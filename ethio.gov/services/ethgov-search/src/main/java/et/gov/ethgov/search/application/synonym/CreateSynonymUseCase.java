package et.gov.ethgov.search.application.synonym;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymId;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSynonymUseCase {
    private final SynonymRepository repository;
    private final Clock clock;

    public CreateSynonymUseCase(SynonymRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SynonymId execute(CreateSynonymCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Synonym aggregate = Synonym.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSynonymCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String synonymId,
            String reason) {}
}
