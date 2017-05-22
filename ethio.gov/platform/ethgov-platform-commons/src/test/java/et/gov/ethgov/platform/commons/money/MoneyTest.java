package et.gov.ethgov.platform.commons.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void addsAndComparesEtb() {
        Money a = Money.etb("10.125");
        Money b = Money.etb(10, 13);
        assertThat(a).isEqualTo(Money.etb("10.12"));
        assertThat(a.plus(b)).isEqualTo(Money.etb("20.25"));
        assertThat(a.compareTo(b)).isLessThan(0);
    }

    @Test
    void appliesHalfEvenRounding() {
        assertThat(Money.etb(new BigDecimal("1.005"))).isEqualTo(Money.etb("1.00"));
        assertThat(Money.etb(new BigDecimal("1.015"))).isEqualTo(Money.etb("1.02"));
    }

    @Test
    void rejectsInvalidMinorUnits() {
        assertThatThrownBy(() -> Money.etb(1, 100)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroAndSigns() {
        assertThat(Money.zeroEtb().isZero()).isTrue();
        assertThat(Money.etb("-1.00").isNegative()).isTrue();
        assertThat(Money.etb("0.01").isPositive()).isTrue();
    }
}
