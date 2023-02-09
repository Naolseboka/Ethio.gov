package et.gov.ethgov.notification.application.notifywebhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryNotifywebhookRepository;
import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotifywebhookApplicationTest {
    private InMemoryNotifywebhookRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotifywebhookRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateNotifywebhookUseCase(repository, clock);
        var activate = new ActivateNotifywebhookUseCase(repository, clock);
        var get = new GetNotifywebhookUseCase(repository);

        var id = create.execute(new CreateNotifywebhookUseCase.CreateNotifywebhookCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateNotifywebhookUseCase.ActivateNotifywebhookCommand(
                null, null, null, null, id.toString(), null));

        Notifywebhook loaded = get.execute(id.toString());
        assertEquals(NotifywebhookStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
