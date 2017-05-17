package et.gov.ethgov.platform.commons.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RegionalStateTest {

    @Test
    void resolvesByCode() {
        assertThat(RegionalState.fromCode("or")).isEqualTo(RegionalState.OROMIA);
        assertThat(RegionalState.ADDIS_ABABA.englishName()).isEqualTo("Addis Ababa");
        assertThatThrownBy(() -> RegionalState.fromCode("ZZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
