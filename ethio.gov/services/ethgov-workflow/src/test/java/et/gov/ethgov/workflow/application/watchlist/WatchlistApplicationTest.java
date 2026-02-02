package et.gov.ethgov.workflow.application.watchlist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryWatchlistRepository;
import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WatchlistApplicationTest {
    private InMemoryWatchlistRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWatchlistRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWatchlistUseCase(repository, clock);
        var activate = new ActivateWatchlistUseCase(repository, clock);
        var get = new GetWatchlistUseCase(repository);

        var id = create.execute(new CreateWatchlistUseCase.CreateWatchlistCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWatchlistUseCase.ActivateWatchlistCommand(
                null, null, null, null, id.toString(), null));

        Watchlist loaded = get.execute(id.toString());
        assertEquals(WatchlistStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
