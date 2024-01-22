package et.gov.ethgov.payments.domain.cutoff;

/** Lifecycle status for Cutoff. */
public enum CutoffStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    ARCHIVED;

    public boolean canTransitionTo(CutoffStatus target) {
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
