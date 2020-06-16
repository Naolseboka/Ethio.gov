package et.gov.ethgov.directory.domain.unit;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Hierarchical government organizational unit within a tenant (ministry → bureau → office).
 */
public final class OrganizationalUnit {

    private final OrganizationalUnitId id;
    private final TenantId tenantId;
    private final UnitCode code;
    private UnitName name;
    private final OrganizationalUnitType type;
    private OrganizationalUnitStatus status;
    private OrganizationalUnitId parentId;
    private String linkedOrganizationId;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<OrganizationalUnitDomainEvent> domainEvents = new ArrayList<>();

    private OrganizationalUnit(
            OrganizationalUnitId id,
            TenantId tenantId,
            UnitCode code,
            UnitName name,
            OrganizationalUnitType type,
            OrganizationalUnitStatus status,
            OrganizationalUnitId parentId,
            String linkedOrganizationId,
            String description,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.status = status;
        this.parentId = parentId;
        this.linkedOrganizationId = linkedOrganizationId;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static OrganizationalUnit create(
            TenantId tenantId,
            UnitCode code,
            UnitName name,
            OrganizationalUnitType type,
            OrganizationalUnitId parentId,
            String linkedOrganizationId,
            String description,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        OrganizationalUnit unit = new OrganizationalUnit(
                OrganizationalUnitId.generate(),
                tenantId,
                code,
                name,
                type,
                OrganizationalUnitStatus.ACTIVE,
                parentId,
                normalizeOrgId(linkedOrganizationId),
                normalizeOptionalText(description, "description", 500),
                now,
                now,
                0);
        unit.record(new OrganizationalUnitCreated(unit.id, tenantId, code, type, now));
        return unit;
    }

    public static OrganizationalUnit rehydrate(
            OrganizationalUnitId id,
            TenantId tenantId,
            UnitCode code,
            UnitName name,
            OrganizationalUnitType type,
            OrganizationalUnitStatus status,
            OrganizationalUnitId parentId,
            String linkedOrganizationId,
            String description,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new OrganizationalUnit(
                id,
                tenantId,
                code,
                name,
                type,
                status,
                parentId,
                linkedOrganizationId,
                description,
                createdAt,
                updatedAt,
                version);
    }

    public void rename(UnitName name, Instant now) {
        ensureMutable();
        Objects.requireNonNull(name, "name");
        if (this.name.equals(name)) {
            return;
        }
        this.name = name;
        touch(now);
        record(new OrganizationalUnitRenamed(id, name, now));
    }

    public void updateDetails(String linkedOrganizationId, String description, Instant now) {
        ensureMutable();
        this.linkedOrganizationId = normalizeOrgId(linkedOrganizationId);
        this.description = normalizeOptionalText(description, "description", 500);
        touch(now);
    }

    public void moveTo(OrganizationalUnitId newParentId, Instant now) {
        ensureMutable();
        if (newParentId != null && newParentId.equals(id)) {
            throw new DomainException(
                    "INVALID_PARENT",
                    "Invalid Parent",
                    "An organizational unit cannot be its own parent",
                    400);
        }
        if (Objects.equals(this.parentId, newParentId)) {
            return;
        }
        this.parentId = newParentId;
        touch(now);
        record(new OrganizationalUnitMoved(id, newParentId, now));
    }

    public void deactivate(Instant now) {
        if (status == OrganizationalUnitStatus.ARCHIVED) {
            throw new DomainException(
                    "UNIT_ARCHIVED",
                    "Unit Archived",
                    "Archived organizational units cannot be deactivated",
                    409);
        }
        if (status == OrganizationalUnitStatus.INACTIVE) {
            return;
        }
        this.status = OrganizationalUnitStatus.INACTIVE;
        touch(now);
        record(new OrganizationalUnitDeactivated(id, now));
    }

    public void reactivate(Instant now) {
        if (status == OrganizationalUnitStatus.ARCHIVED) {
            throw new DomainException(
                    "UNIT_ARCHIVED",
                    "Unit Archived",
                    "Archived organizational units cannot be reactivated",
                    409);
        }
        if (status != OrganizationalUnitStatus.INACTIVE) {
            throw new DomainException(
                    "UNIT_NOT_INACTIVE",
                    "Unit Not Inactive",
                    "Only inactive organizational units can be reactivated",
                    409);
        }
        this.status = OrganizationalUnitStatus.ACTIVE;
        touch(now);
        record(new OrganizationalUnitReactivated(id, now));
    }

    public List<OrganizationalUnitDomainEvent> pullDomainEvents() {
        List<OrganizationalUnitDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureMutable() {
        if (status == OrganizationalUnitStatus.INACTIVE) {
            throw new DomainException(
                    "UNIT_INACTIVE",
                    "Unit Inactive",
                    "Inactive organizational units cannot be modified",
                    409);
        }
        if (status == OrganizationalUnitStatus.ARCHIVED) {
            throw new DomainException(
                    "UNIT_ARCHIVED",
                    "Unit Archived",
                    "Archived organizational units cannot be modified",
                    409);
        }
    }

    private static String normalizeOrgId(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) {
            return null;
        }
        String normalized = organizationId.trim();
        if (!normalized.startsWith("org_") || normalized.length() < 40) {
            throw new IllegalArgumentException("linkedOrganizationId must be an OrganizationId");
        }
        return normalized;
    }

    private static String normalizeOptionalText(String value, String field, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > max) {
            throw new IllegalArgumentException(field + " must be <= " + max + " characters");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(OrganizationalUnitDomainEvent event) {
        domainEvents.add(event);
    }

    public OrganizationalUnitId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public UnitCode code() {
        return code;
    }

    public UnitName name() {
        return name;
    }

    public OrganizationalUnitType type() {
        return type;
    }

    public OrganizationalUnitStatus status() {
        return status;
    }

    public Optional<OrganizationalUnitId> parentId() {
        return Optional.ofNullable(parentId);
    }

    public Optional<String> linkedOrganizationId() {
        return Optional.ofNullable(linkedOrganizationId);
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }
}
