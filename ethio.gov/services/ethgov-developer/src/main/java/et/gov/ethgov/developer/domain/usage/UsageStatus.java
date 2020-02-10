package et.gov.ethgov.developer.domain.usage;

/** Lifecycle status for Usage. */
public enum UsageStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    ARCHIVED;

    public boolean canTransitionTo(UsageStatus target) {
        if (this == target) {
            return true;
        }
        return switch (this) {
            case DRAFT -> target == ACTIVE || target == ARCHIVED;
            case ACTIVE -> target == SUSPENDED || target == CLOSED || target == ARCHIVED;
            case SUSPENDED -> target == ACTIVE || target == CLOSED || target == ARCHIVED;
            case CLOSED -> target == ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}
