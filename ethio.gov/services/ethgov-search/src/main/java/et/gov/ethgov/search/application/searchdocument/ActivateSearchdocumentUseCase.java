package et.gov.ethgov.search.application.searchdocument;

import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentId;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSearchdocumentUseCase {
    private final SearchdocumentRepository repository;
    private final Clock clock;

    public ActivateSearchdocumentUseCase(SearchdocumentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SearchdocumentId execute(ActivateSearchdocumentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SearchdocumentId id = SearchdocumentId.parse(command.searchdocumentId());
        Searchdocument aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("searchdocument not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSearchdocumentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String searchdocumentId,
            String reason) {}
}
