package et.gov.ethgov.search.application.synonym;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymId;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSynonymUseCase {
    private final SynonymRepository repository;
    private final Clock clock;

    public ActivateSynonymUseCase(SynonymRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SynonymId execute(ActivateSynonymCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SynonymId id = SynonymId.parse(command.synonymId());
        Synonym aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("synonym not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSynonymCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String synonymId,
            String reason) {}
}
