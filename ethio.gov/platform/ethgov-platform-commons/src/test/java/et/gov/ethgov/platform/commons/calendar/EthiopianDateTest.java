package et.gov.ethgov.platform.commons.calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EthiopianDateTest {

    @ParameterizedTest
    @CsvSource({
        "2015-01-01, 2022-09-11",
        "2015-13-06, 2023-09-11",
        "2016-01-01, 2023-09-12",
        "1995-01-01, 2002-09-11"
    })
    void convertsKnownGregorianAnchors(String ethiopian, String gregorian) {
        String[] parts = ethiopian.split("-");
        EthiopianDate eth = EthiopianDate.of(
                Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        assertThat(eth.toGregorian()).isEqualTo(LocalDate.parse(gregorian));
        assertThat(EthiopianDate.fromGregorian(LocalDate.parse(gregorian))).isEqualTo(eth);
    }

    @Test
    void rejectsInvalidPagumenDayInCommonYear() {
        assertThat(EthiopianDate.isEthiopianLeapYear(2014)).isFalse();
        assertThatThrownBy(() -> EthiopianDate.of(2014, 13, 6))
                .isInstanceOf(java.time.DateTimeException.class);
    }

    @Test
    void acceptsPagumen6InLeapYear() {
        EthiopianDate leapPagumen = EthiopianDate.of(2015, 13, 6);
        assertThat(leapPagumen.isLeapYear()).isTrue();
        assertThat(leapPagumen.toGregorian()).isEqualTo(LocalDate.of(2023, 9, 11));
    }

    @Test
    void roundTripsAcrossYearBoundary() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        for (int i = 0; i < 800; i++) {
            LocalDate gregorian = start.plusDays(i);
            EthiopianDate eth = EthiopianDate.fromGregorian(gregorian);
            assertThat(eth.toGregorian()).isEqualTo(gregorian);
        }
    }
}
