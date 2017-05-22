package et.gov.ethgov.platform.commons.idempotency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class IdempotencyKeyTest {

    @Test
    void acceptsValidKey() {
        assertThat(IdempotencyKey.of("req-12345").value()).isEqualTo("req-12345");
    }

    @Test
    void rejectsTooShort() {
        assertThatThrownBy(() -> IdempotencyKey.of("short"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
