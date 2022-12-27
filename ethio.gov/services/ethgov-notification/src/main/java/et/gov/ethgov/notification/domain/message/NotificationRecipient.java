package et.gov.ethgov.notification.domain.message;

import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class NotificationRecipient {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String phoneE164;
    private final String email;
    private final String deviceToken;

    private NotificationRecipient(String phoneE164, String email, String deviceToken) {
        this.phoneE164 = phoneE164;
        this.email = email;
        this.deviceToken = deviceToken;
    }

    public static NotificationRecipient forChannel(
            NotificationChannel channel, String phone, String email, String deviceToken) {
        Objects.requireNonNull(channel, "channel");
        return switch (channel) {
            case SMS -> {
                EthiopianPhoneNumber parsed = EthiopianPhoneNumber.parse(require(phone, "phone"));
                yield new NotificationRecipient(parsed.e164(), null, null);
            }
            case EMAIL -> {
                String normalized = require(email, "email").trim().toLowerCase(Locale.ROOT);
                if (!EMAIL.matcher(normalized).matches()) {
                    throw new IllegalArgumentException("email is invalid");
                }
                yield new NotificationRecipient(null, normalized, null);
            }
            case PUSH -> {
                String token = require(deviceToken, "deviceToken").trim();
                if (token.length() < 16 || token.length() > 512) {
                    throw new IllegalArgumentException("deviceToken length must be between 16 and 512");
                }
                yield new NotificationRecipient(null, null, token);
            }
        };
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required for this channel");
        }
        return value;
    }

    public Optional<String> phoneE164() { return Optional.ofNullable(phoneE164); }
    public Optional<String> email() { return Optional.ofNullable(email); }
    public Optional<String> deviceToken() { return Optional.ofNullable(deviceToken); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationRecipient that)) return false;
        return Objects.equals(phoneE164, that.phoneE164)
                && Objects.equals(email, that.email)
                && Objects.equals(deviceToken, that.deviceToken);
    }
    @Override public int hashCode() { return Objects.hash(phoneE164, email, deviceToken); }
}
