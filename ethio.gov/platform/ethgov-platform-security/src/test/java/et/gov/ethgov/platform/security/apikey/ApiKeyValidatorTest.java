package et.gov.ethgov.platform.security.apikey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiKeyValidatorTest {
    @Test
    void beginAndComplete() {
        ApiKeyValidator subject = new ApiKeyValidator();
        String token = subject.begin("demo");
        assertTrue(subject.complete("demo", token));
        assertFalse(subject.complete("demo", token));
    }
}
