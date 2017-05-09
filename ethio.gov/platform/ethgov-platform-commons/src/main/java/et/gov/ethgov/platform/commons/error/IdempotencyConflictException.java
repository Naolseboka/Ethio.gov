package et.gov.ethgov.platform.commons.error;

/**
 * Thrown when a client retries a mutating request with an Idempotency-Key that conflicts with a
 * previously recorded request payload.
 */
public final class IdempotencyConflictException extends DomainException {

    public IdempotencyConflictException(String detail) {
        super(
                "IDEMPOTENCY_CONFLICT",
                "Idempotency Conflict",
                detail,
                409);
    }
}
