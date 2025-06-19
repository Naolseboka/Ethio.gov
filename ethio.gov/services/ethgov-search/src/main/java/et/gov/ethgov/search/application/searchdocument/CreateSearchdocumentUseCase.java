package et.gov.ethgov.search.application.searchdocument;

import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentId;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSearchdocumentUseCase {
    private final SearchdocumentRepository repository;
    private final Clock clock;

    public CreateSearchdocumentUseCase(SearchdocumentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SearchdocumentId execute(CreateSearchdocumentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Searchdocument aggregate = Searchdocument.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSearchdocumentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String searchdocumentId,
            String reason) {}
}
