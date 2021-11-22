package et.gov.ethgov.identity.domain.device;

/** Lifecycle status for Device. */
public enum DeviceStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    ARCHIVED;

    public boolean canTransitionTo(DeviceStatus target) {
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
