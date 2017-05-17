package et.gov.ethgov.platform.commons.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EthiopianAddressTest {

    @Test
    void buildsValidAddress() {
        EthiopianAddress address = EthiopianAddress.builder()
                .regionalState(RegionalState.ADDIS_ABABA)
                .zone("AA-Bole", "Bole")
                .woreda("AA-Bole-03", "Woreda 03")
                .kebele("AA-Bole-03-05", "Kebele 05")
                .street("Africa Avenue")
                .houseNumber("214")
                .build();

        assertThat(address.regionalState()).isEqualTo(RegionalState.ADDIS_ABABA);
        assertThat(address.zoneCode()).isEqualTo("AA-BOLE");
        assertThat(address.street()).isEqualTo("Africa Avenue");
    }

    @Test
    void requiresAdministrativeHierarchy() {
        assertThatThrownBy(() -> EthiopianAddress.builder()
                        .regionalState(RegionalState.OROMIA)
                        .zone("OR-SHEWA", "East Shewa")
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
