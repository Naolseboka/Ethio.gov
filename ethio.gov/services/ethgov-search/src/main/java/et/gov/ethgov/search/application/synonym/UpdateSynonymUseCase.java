package et.gov.ethgov.search.application.synonym;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymId;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSynonymUseCase {
    private final SynonymRepository repository;
    private final Clock clock;

    public UpdateSynonymUseCase(SynonymRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SynonymId execute(UpdateSynonymCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SynonymId id = SynonymId.parse(command.synonymId());
        Synonym aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("synonym not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSynonymCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String synonymId,
            String reason) {}
}
