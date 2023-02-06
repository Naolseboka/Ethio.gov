package et.gov.ethgov.notification.application.dispatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryDispatchRepository;
import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DispatchApplicationTest {
    private InMemoryDispatchRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDispatchRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDispatchUseCase(repository, clock);
        var activate = new ActivateDispatchUseCase(repository, clock);
        var get = new GetDispatchUseCase(repository);

        var id = create.execute(new CreateDispatchUseCase.CreateDispatchCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDispatchUseCase.ActivateDispatchCommand(
                null, null, null, null, id.toString(), null));

        Dispatch loaded = get.execute(id.toString());
        assertEquals(DispatchStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
