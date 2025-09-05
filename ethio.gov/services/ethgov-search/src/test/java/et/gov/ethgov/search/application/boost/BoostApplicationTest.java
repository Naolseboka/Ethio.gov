package et.gov.ethgov.search.application.boost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryBoostRepository;
import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoostApplicationTest {
    private InMemoryBoostRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBoostRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBoostUseCase(repository, clock);
        var activate = new ActivateBoostUseCase(repository, clock);
        var get = new GetBoostUseCase(repository);

        var id = create.execute(new CreateBoostUseCase.CreateBoostCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBoostUseCase.ActivateBoostCommand(
                null, null, null, null, id.toString(), null));

        Boost loaded = get.execute(id.toString());
        assertEquals(BoostStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
