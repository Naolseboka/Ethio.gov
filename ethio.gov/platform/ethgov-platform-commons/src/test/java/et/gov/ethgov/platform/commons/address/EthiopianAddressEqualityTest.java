package et.gov.ethgov.platform.commons.address;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EthiopianAddressEqualityTest {

    @Test
    void equalityIgnoresDisplayNames() {
        EthiopianAddress a = EthiopianAddress.builder()
                .regionalState(RegionalState.TIGRAY)
                .zone("TI-M", "Mekelle")
                .woreda("TI-M-1", "Woreda 1")
                .kebele("TI-M-1-1", "Kebele 1")
                .houseNumber("12")
                .build();
        EthiopianAddress b = EthiopianAddress.builder()
                .regionalState(RegionalState.TIGRAY)
                .zone("ti-m", "Different Label")
                .woreda("ti-m-1", "Other")
                .kebele("ti-m-1-1", "Other")
                .houseNumber("12")
                .build();
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
