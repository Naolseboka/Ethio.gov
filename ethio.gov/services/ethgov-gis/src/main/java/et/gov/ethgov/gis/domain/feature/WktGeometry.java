package et.gov.ethgov.gis.domain.feature;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Well-known text geometry in SRID 4326 (WGS84). Full topology validation is deferred to PostGIS.
 */
public final class WktGeometry {
    public static final int SRID = 4326;
    private static final Pattern ALLOWED = Pattern.compile(
            "^(POINT|LINESTRING|POLYGON|MULTIPOINT|MULTILINESTRING|MULTIPOLYGON)\\s*(\\(.*\\))$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final String wkt;

    private WktGeometry(String wkt) { this.wkt = wkt; }

    public static WktGeometry of(String raw) {
        Objects.requireNonNull(raw, "wkt");
        String n = raw.trim();
        if (n.length() < 10 || n.length() > 200_000) {
            throw new IllegalArgumentException("wkt length must be between 10 and 200000");
        }
        Matcher matcher = ALLOWED.matcher(n);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "wkt must be POINT, LINESTRING, POLYGON, or Multi* variant");
        }
        String type = matcher.group(1).toUpperCase(Locale.ROOT);
        String body = matcher.group(2);
        return new WktGeometry(type + body);
    }

    public String wkt() { return wkt; }
    public int srid() { return SRID; }

    @Override public boolean equals(Object o) {
        return this == o || (o instanceof WktGeometry that && wkt.equals(that.wkt));
    }
    @Override public int hashCode() { return wkt.hashCode(); }
    @Override public String toString() { return wkt; }
}
