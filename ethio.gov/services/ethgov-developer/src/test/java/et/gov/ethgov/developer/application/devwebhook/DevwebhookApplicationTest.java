package et.gov.ethgov.developer.application.devwebhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDevwebhookRepository;
import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DevwebhookApplicationTest {
    private InMemoryDevwebhookRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDevwebhookRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDevwebhookUseCase(repository, clock);
        var activate = new ActivateDevwebhookUseCase(repository, clock);
        var get = new GetDevwebhookUseCase(repository);

        var id = create.execute(new CreateDevwebhookUseCase.CreateDevwebhookCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDevwebhookUseCase.ActivateDevwebhookCommand(
                null, null, null, null, id.toString(), null));

        Devwebhook loaded = get.execute(id.toString());
        assertEquals(DevwebhookStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
