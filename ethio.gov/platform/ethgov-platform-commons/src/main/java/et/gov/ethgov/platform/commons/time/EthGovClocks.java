package et.gov.ethgov.platform.commons.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Ethiopia platform clock defaults. All services must use Africa/Addis_Ababa unless a use case
 * explicitly requires UTC storage (audit timestamps still record Instant / UTC).
 */
public final class EthGovClocks {

    public static final ZoneId ADDIS_ABABA = ZoneId.of("Africa/Addis_Ababa");

    private EthGovClocks() {}

    public static Clock addisAbaba() {
        return Clock.system(ADDIS_ABABA);
    }

    public static Clock fixedAddisAbaba(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return Clock.fixed(instant, ADDIS_ABABA);
    }

    public static ZonedDateTime nowAddisAbaba() {
        return ZonedDateTime.now(ADDIS_ABABA);
    }

    public static LocalDate todayAddisAbaba() {
        return LocalDate.now(ADDIS_ABABA);
    }
}
