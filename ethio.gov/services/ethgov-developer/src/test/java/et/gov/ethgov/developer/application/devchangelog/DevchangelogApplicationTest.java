package et.gov.ethgov.developer.application.devchangelog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDevchangelogRepository;
import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevchangelogApplicationTest {
    private InMemoryDevchangelogRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDevchangelogRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDevchangelogUseCase(repository, clock);
        var activate = new ActivateDevchangelogUseCase(repository, clock);
        var get = new GetDevchangelogUseCase(repository);

        var id = create.execute(new CreateDevchangelogUseCase.CreateDevchangelogCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDevchangelogUseCase.ActivateDevchangelogCommand(
                null, null, null, null, id.toString(), null));

        Devchangelog loaded = get.execute(id.toString());
        assertEquals(DevchangelogStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
