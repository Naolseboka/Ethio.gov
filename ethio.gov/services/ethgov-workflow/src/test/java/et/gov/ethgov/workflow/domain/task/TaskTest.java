package et.gov.ethgov.workflow.domain.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TaskTest {
    private final Instant now = Instant.parse("2024-01-15T10:15:30Z");

    @Test
    void createStartsInDraft() {
        Task aggregate = Task.create("REF-1", "Sample", "desc", "tenant-a", now);
        assertEquals(TaskStatus.DRAFT, aggregate.status());
        assertEquals("REF-1", aggregate.referenceCode());
        assertTrue(aggregate.version() == 0L);
    }

    @Test
    void activateFromDraft() {
        Task aggregate = Task.create("REF-2", "Sample", null, "tenant-a", now);
        aggregate.activate(now.plusSeconds(5));
        assertEquals(TaskStatus.ACTIVE, aggregate.status());
        assertEquals(1L, aggregate.version());
    }

    @Test
    void rejectBlankReference() {
        assertThrows(IllegalArgumentException.class,
                () -> Task.create("  ", "Sample", null, "tenant-a", now));
    }

    @Test
    void suspendRequiresReason() {
        Task aggregate = Task.create("REF-3", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        assertThrows(IllegalArgumentException.class, () -> aggregate.suspend(" ", now));
    }

    @Test
    void illegalTransitionThrows() {
        Task aggregate = Task.create("REF-4", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        aggregate.close(now.plusSeconds(1));
        assertThrows(IllegalStateException.class, () -> aggregate.activate(now.plusSeconds(2)));
    }
}
