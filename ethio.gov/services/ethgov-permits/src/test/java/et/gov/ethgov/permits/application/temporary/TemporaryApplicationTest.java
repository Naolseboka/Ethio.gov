package et.gov.ethgov.permits.application.temporary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryTemporaryRepository;
import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemporaryApplicationTest {
    private InMemoryTemporaryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTemporaryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTemporaryUseCase(repository, clock);
        var activate = new ActivateTemporaryUseCase(repository, clock);
        var get = new GetTemporaryUseCase(repository);

        var id = create.execute(new CreateTemporaryUseCase.CreateTemporaryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTemporaryUseCase.ActivateTemporaryCommand(
                null, null, null, null, id.toString(), null));

        Temporary loaded = get.execute(id.toString());
        assertEquals(TemporaryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
