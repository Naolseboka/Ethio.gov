package et.gov.ethgov.reporting.application.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryScheduleRepository;
import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScheduleApplicationTest {
    private InMemoryScheduleRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryScheduleRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateScheduleUseCase(repository, clock);
        var activate = new ActivateScheduleUseCase(repository, clock);
        var get = new GetScheduleUseCase(repository);

        var id = create.execute(new CreateScheduleUseCase.CreateScheduleCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateScheduleUseCase.ActivateScheduleCommand(
                null, null, null, null, id.toString(), null));

        Schedule loaded = get.execute(id.toString());
        assertEquals(ScheduleStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
