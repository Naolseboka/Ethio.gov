package et.gov.ethgov.platform.commons.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class EthGovClocksTest {

    @Test
    void usesAddisAbabaZone() {
        assertThat(EthGovClocks.ADDIS_ABABA).isEqualTo(ZoneId.of("Africa/Addis_Ababa"));
        Instant fixed = Instant.parse("2024-01-01T00:00:00Z");
        assertThat(EthGovClocks.fixedAddisAbaba(fixed).instant()).isEqualTo(fixed);
        assertThat(EthGovClocks.todayAddisAbaba()).isNotNull();
        assertThat(EthGovClocks.nowAddisAbaba().getZone()).isEqualTo(EthGovClocks.ADDIS_ABABA);
    }
}
