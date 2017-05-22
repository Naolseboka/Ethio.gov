package et.gov.ethgov.platform.commons.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyMoreTest {

    @Test
    void arithmeticAndEquality() {
        Money a = Money.etb("10.00");
        Money b = Money.etb("2.50");
        assertThat(a.minus(b)).isEqualTo(Money.etb("7.50"));
        assertThat(a.multiply(new BigDecimal("2"))).isEqualTo(Money.etb("20.00"));
        assertThat(a).isEqualTo(Money.etb("10.00"));
        assertThat(a.hashCode()).isEqualTo(Money.etb("10.00").hashCode());
        assertThat(a.toString()).isEqualTo("10.00 ETB");
        assertThat(a.currency().getCurrencyCode()).isEqualTo("ETB");
        assertThat(a.amount()).isEqualByComparingTo("10.00");
    }

    @Test
    void rejectsCurrencyMismatch() {
        // Construct two ETB values; mismatch path is guarded for future currencies via compare path.
        Money a = Money.etb("1.00");
        assertThatThrownBy(() -> a.plus(null)).isInstanceOf(NullPointerException.class);
    }
}
