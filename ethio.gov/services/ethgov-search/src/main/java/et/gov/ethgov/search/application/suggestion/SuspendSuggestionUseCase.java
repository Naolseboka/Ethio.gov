package et.gov.ethgov.search.application.suggestion;

import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionId;
import et.gov.ethgov.search.domain.suggestion.SuggestionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSuggestionUseCase {
    private final SuggestionRepository repository;
    private final Clock clock;

    public SuspendSuggestionUseCase(SuggestionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuggestionId execute(SuspendSuggestionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SuggestionId id = SuggestionId.parse(command.suggestionId());
        Suggestion aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suggestion not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSuggestionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suggestionId,
            String reason) {}
}
