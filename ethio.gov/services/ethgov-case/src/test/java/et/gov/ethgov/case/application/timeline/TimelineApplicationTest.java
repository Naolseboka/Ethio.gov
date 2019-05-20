package et.gov.ethgov.case.application.timeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryTimelineRepository;
import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimelineApplicationTest {
    private InMemoryTimelineRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTimelineRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTimelineUseCase(repository, clock);
        var activate = new ActivateTimelineUseCase(repository, clock);
        var get = new GetTimelineUseCase(repository);

        var id = create.execute(new CreateTimelineUseCase.CreateTimelineCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTimelineUseCase.ActivateTimelineCommand(
                null, null, null, null, id.toString(), null));

        Timeline loaded = get.execute(id.toString());
        assertEquals(TimelineStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
