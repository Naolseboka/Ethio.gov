package et.gov.ethgov.analytics.application.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemorySegmentRepository;
import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SegmentApplicationTest {
    private InMemorySegmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySegmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSegmentUseCase(repository, clock);
        var activate = new ActivateSegmentUseCase(repository, clock);
        var get = new GetSegmentUseCase(repository);

        var id = create.execute(new CreateSegmentUseCase.CreateSegmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSegmentUseCase.ActivateSegmentCommand(
                null, null, null, null, id.toString(), null));

        Segment loaded = get.execute(id.toString());
        assertEquals(SegmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
