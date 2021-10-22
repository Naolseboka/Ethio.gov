package et.gov.ethgov.identity.domain.account;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Password policy for interactive EthGov accounts.
 * Hashing is performed outside the domain via {@link PasswordHasher}; this type validates plaintext
 * before hashing and stores only the resulting hash string inside the aggregate.
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 12;
    public static final int MAX_LENGTH = 128;
    public static final int MAX_FAILED_ATTEMPTS = 5;

    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("[0-9]");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    private PasswordPolicy() {}

    public static void validatePlaintext(String plaintext) {
        Objects.requireNonNull(plaintext, "password");
        if (plaintext.length() < MIN_LENGTH || plaintext.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "password length must be between " + MIN_LENGTH + " and " + MAX_LENGTH);
        }
        if (!HAS_UPPER.matcher(plaintext).find()
                || !HAS_LOWER.matcher(plaintext).find()
                || !HAS_DIGIT.matcher(plaintext).find()
                || !HAS_SPECIAL.matcher(plaintext).find()) {
            throw new IllegalArgumentException(
                    "password must include upper, lower, digit, and special characters");
        }
    }
}
