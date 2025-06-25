package et.gov.ethgov.search.application.suggestion;

import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionId;
import et.gov.ethgov.search.domain.suggestion.SuggestionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSuggestionUseCase {
    private final SuggestionRepository repository;
    private final Clock clock;

    public CreateSuggestionUseCase(SuggestionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuggestionId execute(CreateSuggestionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Suggestion aggregate = Suggestion.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSuggestionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suggestionId,
            String reason) {}
}
