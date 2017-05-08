package et.gov.ethgov.platform.commons.contact;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ethiopian phone number in E.164 form ({@code +251...}).
 * Accepts common local forms and normalizes to a canonical mobile or fixed-line number.
 */
public final class EthiopianPhoneNumber {

    private static final Pattern E164 = Pattern.compile("^\\+251[1-9]\\d{8}$");
    private static final Pattern LOCAL_MOBILE = Pattern.compile("^0?(9\\d{8})$");
    private static final Pattern LOCAL_FIXED = Pattern.compile("^0?([1-8]\\d{8})$");
    private static final Pattern INTL_NO_PLUS = Pattern.compile("^251([1-9]\\d{8})$");

    private final String e164;

    private EthiopianPhoneNumber(String e164) {
        this.e164 = e164;
    }

    public static EthiopianPhoneNumber parse(String raw) {
        Objects.requireNonNull(raw, "phoneNumber");
        String digits = raw.trim().replaceAll("[\\s\\-()]", "");

        if (E164.matcher(digits).matches()) {
            return new EthiopianPhoneNumber(digits);
        }

        Matcher intl = INTL_NO_PLUS.matcher(digits);
        if (intl.matches()) {
            return new EthiopianPhoneNumber("+251" + intl.group(1));
        }

        Matcher mobile = LOCAL_MOBILE.matcher(digits);
        if (mobile.matches()) {
            return new EthiopianPhoneNumber("+251" + mobile.group(1));
        }

        Matcher fixed = LOCAL_FIXED.matcher(digits);
        if (fixed.matches()) {
            return new EthiopianPhoneNumber("+251" + fixed.group(1));
        }

        throw new IllegalArgumentException("Invalid Ethiopian phone number: " + raw);
    }

    public String e164() {
        return e164;
    }

    public String national() {
        return "0" + e164.substring(4);
    }

    public boolean isMobile() {
        return e164.charAt(4) == '9';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EthiopianPhoneNumber that)) {
            return false;
        }
        return e164.equals(that.e164);
    }

    @Override
    public int hashCode() {
        return e164.hashCode();
    }

    @Override
    public String toString() {
        return e164;
    }
}
