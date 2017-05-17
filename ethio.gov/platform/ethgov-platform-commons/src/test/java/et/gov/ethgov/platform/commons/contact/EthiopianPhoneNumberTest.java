package et.gov.ethgov.platform.commons.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EthiopianPhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"0911234567", "911234567", "+251911234567", "251911234567", "0911 234 567"})
    void normalizesMobileNumbers(String raw) {
        EthiopianPhoneNumber phone = EthiopianPhoneNumber.parse(raw);
        assertThat(phone.e164()).isEqualTo("+251911234567");
        assertThat(phone.national()).isEqualTo("0911234567");
        assertThat(phone.isMobile()).isTrue();
    }

    @Test
    void normalizesFixedLine() {
        EthiopianPhoneNumber phone = EthiopianPhoneNumber.parse("0115517171");
        assertThat(phone.e164()).isEqualTo("+251115517171");
        assertThat(phone.isMobile()).isFalse();
    }

    @Test
    void rejectsInvalid() {
        assertThatThrownBy(() -> EthiopianPhoneNumber.parse("12345"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
