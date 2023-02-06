package et.gov.ethgov.notification.application.notifychannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryNotifychannelRepository;
import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotifychannelApplicationTest {
    private InMemoryNotifychannelRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotifychannelRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateNotifychannelUseCase(repository, clock);
        var activate = new ActivateNotifychannelUseCase(repository, clock);
        var get = new GetNotifychannelUseCase(repository);

        var id = create.execute(new CreateNotifychannelUseCase.CreateNotifychannelCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateNotifychannelUseCase.ActivateNotifychannelCommand(
                null, null, null, null, id.toString(), null));

        Notifychannel loaded = get.execute(id.toString());
        assertEquals(NotifychannelStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
