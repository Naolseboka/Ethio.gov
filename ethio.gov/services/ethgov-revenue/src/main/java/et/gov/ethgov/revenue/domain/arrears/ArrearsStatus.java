package et.gov.ethgov.revenue.domain.arrears;

/** Lifecycle status for Arrears. */
public enum ArrearsStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    ARCHIVED;

    public boolean canTransitionTo(ArrearsStatus target) {
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
