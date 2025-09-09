package et.gov.ethgov.search.application.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryRankingRepository;
import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RankingApplicationTest {
    private InMemoryRankingRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRankingRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRankingUseCase(repository, clock);
        var activate = new ActivateRankingUseCase(repository, clock);
        var get = new GetRankingUseCase(repository);

        var id = create.execute(new CreateRankingUseCase.CreateRankingCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRankingUseCase.ActivateRankingCommand(
                null, null, null, null, id.toString(), null));

        Ranking loaded = get.execute(id.toString());
        assertEquals(RankingStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
