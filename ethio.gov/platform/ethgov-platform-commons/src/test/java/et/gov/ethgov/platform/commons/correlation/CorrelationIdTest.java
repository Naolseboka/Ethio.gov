package et.gov.ethgov.platform.commons.correlation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CorrelationIdTest {

    @AfterEach
    void clear() {
        CorrelationId.clear();
    }

    @Test
    void generatesAndParsesValidIds() {
        CorrelationId generated = CorrelationId.generate();
        assertThat(generated.value()).hasSize(36);
        assertThat(CorrelationId.of(generated.value())).isEqualTo(generated);
    }

    @Test
    void requireOrGenerateBindsThreadLocal() {
        CorrelationId first = CorrelationId.requireOrGenerate();
        CorrelationId second = CorrelationId.requireOrGenerate();
        assertThat(first).isEqualTo(second);
        assertThat(CorrelationId.current()).contains(first);
    }

    @Test
    void rejectsInvalidFormat() {
        assertThatThrownBy(() -> CorrelationId.of("bad"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
