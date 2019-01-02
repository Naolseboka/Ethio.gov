package et.gov.ethgov.analytics.application.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryEventRepository;
import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventApplicationTest {
    private InMemoryEventRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEventRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEventUseCase(repository, clock);
        var activate = new ActivateEventUseCase(repository, clock);
        var get = new GetEventUseCase(repository);

        var id = create.execute(new CreateEventUseCase.CreateEventCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEventUseCase.ActivateEventCommand(
                null, null, null, null, id.toString(), null));

        Event loaded = get.execute(id.toString());
        assertEquals(EventStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
