package et.gov.ethgov.platform.commons.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base domain/application exception carrying an RFC7807 problem type key and stable error code.
 */
public class DomainException extends RuntimeException {

    private final String errorCode;
    private final String title;
    private final int status;
    private final Map<String, Object> properties;

    public DomainException(String errorCode, String title, String detail, int status) {
        this(errorCode, title, detail, status, Map.of());
    }

    public DomainException(
            String errorCode, String title, String detail, int status, Map<String, Object> properties) {
        super(detail);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
        this.title = Objects.requireNonNull(title, "title");
        if (status < 400 || status > 599) {
            throw new IllegalArgumentException("status must be an HTTP error status");
        }
        this.status = status;
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    public String errorCode() {
        return errorCode;
    }

    public String title() {
        return title;
    }

    public int status() {
        return status;
    }

    public Map<String, Object> properties() {
        return properties;
    }
}
