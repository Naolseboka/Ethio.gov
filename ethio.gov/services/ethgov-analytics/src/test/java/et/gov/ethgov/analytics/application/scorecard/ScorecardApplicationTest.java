package et.gov.ethgov.analytics.application.scorecard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryScorecardRepository;
import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScorecardApplicationTest {
    private InMemoryScorecardRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryScorecardRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateScorecardUseCase(repository, clock);
        var activate = new ActivateScorecardUseCase(repository, clock);
        var get = new GetScorecardUseCase(repository);

        var id = create.execute(new CreateScorecardUseCase.CreateScorecardCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateScorecardUseCase.ActivateScorecardCommand(
                null, null, null, null, id.toString(), null));

        Scorecard loaded = get.execute(id.toString());
        assertEquals(ScorecardStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
