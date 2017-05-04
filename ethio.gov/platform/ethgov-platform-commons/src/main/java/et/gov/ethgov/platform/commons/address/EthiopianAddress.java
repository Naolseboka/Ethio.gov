package et.gov.ethgov.platform.commons.address;

import java.util.Objects;

/**
 * Structured Ethiopian administrative address down to kebele.
 * Zone/woreda/kebele names are tenant-owned reference data; this value object stores the
 * hierarchical codes and human-readable labels resolved at write time.
 */
public final class EthiopianAddress {

    private final RegionalState regionalState;
    private final String zoneCode;
    private final String zoneName;
    private final String woredaCode;
    private final String woredaName;
    private final String kebeleCode;
    private final String kebeleName;
    private final String locality;
    private final String street;
    private final String houseNumber;
    private final String postalCode;

    private EthiopianAddress(Builder builder) {
        this.regionalState = Objects.requireNonNull(builder.regionalState, "regionalState");
        this.zoneCode = requireCode(builder.zoneCode, "zoneCode");
        this.zoneName = requireName(builder.zoneName, "zoneName");
        this.woredaCode = requireCode(builder.woredaCode, "woredaCode");
        this.woredaName = requireName(builder.woredaName, "woredaName");
        this.kebeleCode = requireCode(builder.kebeleCode, "kebeleCode");
        this.kebeleName = requireName(builder.kebeleName, "kebeleName");
        this.locality = blankToNull(builder.locality);
        this.street = blankToNull(builder.street);
        this.houseNumber = blankToNull(builder.houseNumber);
        this.postalCode = blankToNull(builder.postalCode);
    }

    public static Builder builder() {
        return new Builder();
    }

    public RegionalState regionalState() {
        return regionalState;
    }

    public String zoneCode() {
        return zoneCode;
    }

    public String zoneName() {
        return zoneName;
    }

    public String woredaCode() {
        return woredaCode;
    }

    public String woredaName() {
        return woredaName;
    }

    public String kebeleCode() {
        return kebeleCode;
    }

    public String kebeleName() {
        return kebeleName;
    }

    public String locality() {
        return locality;
    }

    public String street() {
        return street;
    }

    public String houseNumber() {
        return houseNumber;
    }

    public String postalCode() {
        return postalCode;
    }

    private static String requireCode(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        String normalized = value.trim().toUpperCase();
        if (normalized.length() > 32) {
            throw new IllegalArgumentException(field + " must be <= 32 characters");
        }
        return normalized;
    }

    private static String requireName(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        String normalized = value.trim();
        if (normalized.length() > 128) {
            throw new IllegalArgumentException(field + " must be <= 128 characters");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EthiopianAddress that)) {
            return false;
        }
        return regionalState == that.regionalState
                && zoneCode.equals(that.zoneCode)
                && woredaCode.equals(that.woredaCode)
                && kebeleCode.equals(that.kebeleCode)
                && Objects.equals(locality, that.locality)
                && Objects.equals(street, that.street)
                && Objects.equals(houseNumber, that.houseNumber)
                && Objects.equals(postalCode, that.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                regionalState, zoneCode, woredaCode, kebeleCode, locality, street, houseNumber, postalCode);
    }

    public static final class Builder {
        private RegionalState regionalState;
        private String zoneCode;
        private String zoneName;
        private String woredaCode;
        private String woredaName;
        private String kebeleCode;
        private String kebeleName;
        private String locality;
        private String street;
        private String houseNumber;
        private String postalCode;

        private Builder() {}

        public Builder regionalState(RegionalState regionalState) {
            this.regionalState = regionalState;
            return this;
        }

        public Builder zone(String code, String name) {
            this.zoneCode = code;
            this.zoneName = name;
            return this;
        }

        public Builder woreda(String code, String name) {
            this.woredaCode = code;
            this.woredaName = name;
            return this;
        }

        public Builder kebele(String code, String name) {
            this.kebeleCode = code;
            this.kebeleName = name;
            return this;
        }

        public Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public EthiopianAddress build() {
            return new EthiopianAddress(this);
        }
    }
}
