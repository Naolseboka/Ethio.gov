package et.gov.ethgov.platform.persistence.outbox;

/**
 * Delivery state for transactional outbox rows.
 */
public enum OutboxStatus {
    PENDING,
    PUBLISHING,
    PUBLISHED,
    FAILED
}
