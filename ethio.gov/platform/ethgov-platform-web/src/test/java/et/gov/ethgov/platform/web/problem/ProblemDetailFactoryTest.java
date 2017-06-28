package et.gov.ethgov.platform.web.problem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProblemDetailFactoryTest {
    @Test
    void beginAndComplete() {
        ProblemDetailFactory subject = new ProblemDetailFactory();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
