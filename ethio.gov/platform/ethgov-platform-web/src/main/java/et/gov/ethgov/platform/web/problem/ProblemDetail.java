package et.gov.ethgov.platform.web.problem;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RFC 7807 Problem Details payload used by all EthGov HTTP APIs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProblemDetail {

    private final URI type;
    private final String title;
    private final int status;
    private final String detail;
    private final URI instance;
    private final String errorCode;
    private final String correlationId;
    private final String tenantId;
    private final Instant timestamp;
    private final Map<String, Object> extensions;

    private ProblemDetail(Builder builder) {
        this.type = Objects.requireNonNull(builder.type, "type");
        this.title = Objects.requireNonNull(builder.title, "title");
        this.status = builder.status;
        this.detail = builder.detail;
        this.instance = builder.instance;
        this.errorCode = Objects.requireNonNull(builder.errorCode, "errorCode");
        this.correlationId = builder.correlationId;
        this.tenantId = builder.tenantId;
        this.timestamp = Objects.requireNonNullElse(builder.timestamp, Instant.now());
        this.extensions = Map.copyOf(builder.extensions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public URI getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public URI getInstance() {
        return instance;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public static final class Builder {
        private URI type = URI.create("about:blank");
        private String title;
        private int status;
        private String detail;
        private URI instance;
        private String errorCode;
        private String correlationId;
        private String tenantId;
        private Instant timestamp;
        private final Map<String, Object> extensions = new LinkedHashMap<>();

        private Builder() {}

        public Builder type(URI type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(URI instance) {
            this.instance = instance;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder extension(String key, Object value) {
            this.extensions.put(key, value);
            return this;
        }

        public Builder extensions(Map<String, Object> values) {
            this.extensions.putAll(values);
            return this;
        }

        public ProblemDetail build() {
            if (status < 400 || status > 599) {
                throw new IllegalArgumentException("status must be HTTP error");
            }
            return new ProblemDetail(this);
        }
    }
}
