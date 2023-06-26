package et.gov.ethgov.opendata.application.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryPublisherRepository;
import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublisherApplicationTest {
    private InMemoryPublisherRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPublisherRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePublisherUseCase(repository, clock);
        var activate = new ActivatePublisherUseCase(repository, clock);
        var get = new GetPublisherUseCase(repository);

        var id = create.execute(new CreatePublisherUseCase.CreatePublisherCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePublisherUseCase.ActivatePublisherCommand(
                null, null, null, null, id.toString(), null));

        Publisher loaded = get.execute(id.toString());
        assertEquals(PublisherStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
