package et.gov.ethgov.organization.domain.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class HierarchyTest {
    private final Instant now = Instant.parse("2024-01-15T10:15:30Z");

    @Test
    void createStartsInDraft() {
        Hierarchy aggregate = Hierarchy.create("REF-1", "Sample", "desc", "tenant-a", now);
        assertEquals(HierarchyStatus.DRAFT, aggregate.status());
        assertEquals("REF-1", aggregate.referenceCode());
        assertTrue(aggregate.version() == 0L);
    }

    @Test
    void activateFromDraft() {
        Hierarchy aggregate = Hierarchy.create("REF-2", "Sample", null, "tenant-a", now);
        aggregate.activate(now.plusSeconds(5));
        assertEquals(HierarchyStatus.ACTIVE, aggregate.status());
        assertEquals(1L, aggregate.version());
    }

    @Test
    void rejectBlankReference() {
        assertThrows(IllegalArgumentException.class,
                () -> Hierarchy.create("  ", "Sample", null, "tenant-a", now));
    }

    @Test
    void suspendRequiresReason() {
        Hierarchy aggregate = Hierarchy.create("REF-3", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        assertThrows(IllegalArgumentException.class, () -> aggregate.suspend(" ", now));
    }

    @Test
    void illegalTransitionThrows() {
        Hierarchy aggregate = Hierarchy.create("REF-4", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        aggregate.close(now.plusSeconds(1));
        assertThrows(IllegalStateException.class, () -> aggregate.activate(now.plusSeconds(2)));
    }
}
