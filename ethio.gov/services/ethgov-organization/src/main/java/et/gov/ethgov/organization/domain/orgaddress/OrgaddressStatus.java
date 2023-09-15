package et.gov.ethgov.organization.domain.orgaddress;

/** Lifecycle status for Orgaddress. */
public enum OrgaddressStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    ARCHIVED;

    public boolean canTransitionTo(OrgaddressStatus target) {
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
