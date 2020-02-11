package et.gov.ethgov.developer.application.devapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDevapplicationRepository;
import et.gov.ethgov.developer.domain.devapplication.Devapplication;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevapplicationApplicationTest {
    private InMemoryDevapplicationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDevapplicationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDevapplicationUseCase(repository, clock);
        var activate = new ActivateDevapplicationUseCase(repository, clock);
        var get = new GetDevapplicationUseCase(repository);

        var id = create.execute(new CreateDevapplicationUseCase.CreateDevapplicationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDevapplicationUseCase.ActivateDevapplicationCommand(
                null, null, null, null, id.toString(), null));

        Devapplication loaded = get.execute(id.toString());
        assertEquals(DevapplicationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
