package et.gov.ethgov.platform.commons.error;

/**
 * Thrown when optimistic locking detects a concurrent modification.
 */
public final class ConcurrentModificationException extends DomainException {

    public ConcurrentModificationException(String resourceType, String resourceId, long expectedVersion) {
        super(
                "CONCURRENT_MODIFICATION",
                "Concurrent Modification",
                resourceType + " '" + resourceId + "' was modified concurrently (expected version "
                        + expectedVersion + ")",
                409,
                java.util.Map.of(
                        "resourceType", resourceType,
                        "resourceId", resourceId,
                        "expectedVersion", expectedVersion));
    }
}
