package et.gov.ethgov.workflow.application.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryTimerRepository;
import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimerApplicationTest {
    private InMemoryTimerRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTimerRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTimerUseCase(repository, clock);
        var activate = new ActivateTimerUseCase(repository, clock);
        var get = new GetTimerUseCase(repository);

        var id = create.execute(new CreateTimerUseCase.CreateTimerCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTimerUseCase.ActivateTimerCommand(
                null, null, null, null, id.toString(), null));

        Timer loaded = get.execute(id.toString());
        assertEquals(TimerStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
