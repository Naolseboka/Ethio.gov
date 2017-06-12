package et.gov.ethgov.platform.persistence.softdelete;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SoftDeleteTest {
    @Test
    void beginAndComplete() {
        SoftDelete subject = new SoftDelete();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
