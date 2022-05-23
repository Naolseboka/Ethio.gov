package et.gov.ethgov.land.domain.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class MutationTest {
    private final Instant now = Instant.parse("2024-01-15T10:15:30Z");

    @Test
    void createStartsInDraft() {
        Mutation aggregate = Mutation.create("REF-1", "Sample", "desc", "tenant-a", now);
        assertEquals(MutationStatus.DRAFT, aggregate.status());
        assertEquals("REF-1", aggregate.referenceCode());
        assertTrue(aggregate.version() == 0L);
    }

    @Test
    void activateFromDraft() {
        Mutation aggregate = Mutation.create("REF-2", "Sample", null, "tenant-a", now);
        aggregate.activate(now.plusSeconds(5));
        assertEquals(MutationStatus.ACTIVE, aggregate.status());
        assertEquals(1L, aggregate.version());
    }

    @Test
    void rejectBlankReference() {
        assertThrows(IllegalArgumentException.class,
                () -> Mutation.create("  ", "Sample", null, "tenant-a", now));
    }

    @Test
    void suspendRequiresReason() {
        Mutation aggregate = Mutation.create("REF-3", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        assertThrows(IllegalArgumentException.class, () -> aggregate.suspend(" ", now));
    }

    @Test
    void illegalTransitionThrows() {
        Mutation aggregate = Mutation.create("REF-4", "Sample", null, "tenant-a", now);
        aggregate.activate(now);
        aggregate.close(now.plusSeconds(1));
        assertThrows(IllegalStateException.class, () -> aggregate.activate(now.plusSeconds(2)));
    }
}
