package et.gov.ethgov.platform.commons.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void concurrentModificationIncludesProperties() {
        ConcurrentModificationException ex =
                new ConcurrentModificationException("Case", "case-1", 3);
        assertThat(ex.status()).isEqualTo(409);
        assertThat(ex.errorCode()).isEqualTo("CONCURRENT_MODIFICATION");
        assertThat(ex.properties()).containsEntry("expectedVersion", 3L);
    }
}
