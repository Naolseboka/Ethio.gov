package et.gov.ethgov.search.application.suggestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemorySuggestionRepository;
import et.gov.ethgov.search.domain.suggestion.Suggestion;
import et.gov.ethgov.search.domain.suggestion.SuggestionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuggestionApplicationTest {
    private InMemorySuggestionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySuggestionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSuggestionUseCase(repository, clock);
        var activate = new ActivateSuggestionUseCase(repository, clock);
        var get = new GetSuggestionUseCase(repository);

        var id = create.execute(new CreateSuggestionUseCase.CreateSuggestionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSuggestionUseCase.ActivateSuggestionCommand(
                null, null, null, null, id.toString(), null));

        Suggestion loaded = get.execute(id.toString());
        assertEquals(SuggestionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
