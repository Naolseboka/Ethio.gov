package et.gov.ethgov.developer.application.devratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDevratelimitRepository;
import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevratelimitApplicationTest {
    private InMemoryDevratelimitRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDevratelimitRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDevratelimitUseCase(repository, clock);
        var activate = new ActivateDevratelimitUseCase(repository, clock);
        var get = new GetDevratelimitUseCase(repository);

        var id = create.execute(new CreateDevratelimitUseCase.CreateDevratelimitCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDevratelimitUseCase.ActivateDevratelimitCommand(
                null, null, null, null, id.toString(), null));

        Devratelimit loaded = get.execute(id.toString());
        assertEquals(DevratelimitStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
