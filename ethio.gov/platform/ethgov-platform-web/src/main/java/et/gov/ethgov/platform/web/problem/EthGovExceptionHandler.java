package et.gov.ethgov.platform.web.problem;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Maps domain and validation failures to RFC7807 problem responses.
 */
@RestControllerAdvice
public class EthGovExceptionHandler {

    public static final String PROBLEM_MEDIA_TYPE = "application/problem+json";
    private static final URI VALIDATION_TYPE = URI.create("https://ethgov.et/problems/validation-failed");
    private static final URI DOMAIN_TYPE = URI.create("https://ethgov.et/problems/domain");
    private static final URI INTERNAL_TYPE = URI.create("https://ethgov.et/problems/internal");

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(DomainException ex, WebRequest request) {
        ProblemDetail body = baseBuilder(ex.status(), ex.title(), ex.getMessage(), ex.errorCode(), request)
                .type(DOMAIN_TYPE.resolve(ex.errorCode().toLowerCase().replace('_', '-')))
                .extensions(ex.properties())
                .build();
        return ResponseEntity.status(ex.status())
                .contentType(MediaType.parseMediaType(PROBLEM_MEDIA_TYPE))
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        ProblemDetail body = baseBuilder(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        "Request validation failed",
                        "VALIDATION_FAILED",
                        request)
                .type(VALIDATION_TYPE)
                .extension("errors", fieldErrors)
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.parseMediaType(PROBLEM_MEDIA_TYPE))
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        ProblemDetail body = baseBuilder(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        "BAD_REQUEST",
                        request)
                .type(URI.create("https://ethgov.et/problems/bad-request"))
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.parseMediaType(PROBLEM_MEDIA_TYPE))
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(Exception ex, WebRequest request) {
        ProblemDetail body = baseBuilder(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "An unexpected error occurred",
                        "INTERNAL_ERROR",
                        request)
                .type(INTERNAL_TYPE)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType(PROBLEM_MEDIA_TYPE))
                .body(body);
    }

    private ProblemDetail.Builder baseBuilder(
            int status, String title, String detail, String errorCode, WebRequest request) {
        ProblemDetail.Builder builder = ProblemDetail.builder()
                .status(status)
                .title(title)
                .detail(detail)
                .errorCode(errorCode)
                .correlationId(CorrelationId.current().map(CorrelationId::value).orElse(null))
                .tenantId(TenantContext.current().map(Object::toString).orElse(null));
        if (request instanceof ServletWebRequest servletWebRequest) {
            builder.instance(URI.create(servletWebRequest.getRequest().getRequestURI()));
        }
        return builder;
    }
}
