package et.gov.ethgov.land.domain.parcel;

/** Urban land in Ethiopia is predominantly leasehold; freehold retained for historical/rural edge cases. */
public enum TenureType {
    LEASEHOLD, FREEHOLD, GOVERNMENT, COMMUNAL, UNKNOWN
}
