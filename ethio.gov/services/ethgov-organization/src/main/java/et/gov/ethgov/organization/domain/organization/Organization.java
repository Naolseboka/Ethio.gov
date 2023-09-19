package et.gov.ethgov.organization.domain.organization;

import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Legal organization registered with EthGov for licensing, tax, and service interactions.
 */
public final class Organization {

    private final OrganizationId id;
    private LegalName legalName;
    private String tradeName;
    private final OrganizationType type;
    private OrganizationStatus status;
    private TinNumber tin;
    private String registrationNumber;
    private EthiopianAddress registeredAddress;
    private EthiopianPhoneNumber phone;
    private String email;
    private final TenantId registeringTenantId;
    private String ownerCitizenId;
    private LocalDate establishedOn;
    private String suspensionReason;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<OrganizationDomainEvent> domainEvents = new ArrayList<>();

    private Organization(
            OrganizationId id,
            LegalName legalName,
            String tradeName,
            OrganizationType type,
            OrganizationStatus status,
            TinNumber tin,
            String registrationNumber,
            EthiopianAddress registeredAddress,
            EthiopianPhoneNumber phone,
            String email,
            TenantId registeringTenantId,
            String ownerCitizenId,
            LocalDate establishedOn,
            String suspensionReason,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.type = type;
        this.status = status;
        this.tin = tin;
        this.registrationNumber = registrationNumber;
        this.registeredAddress = registeredAddress;
        this.phone = phone;
        this.email = email;
        this.registeringTenantId = registeringTenantId;
        this.ownerCitizenId = ownerCitizenId;
        this.establishedOn = establishedOn;
        this.suspensionReason = suspensionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Organization register(
            LegalName legalName,
            String tradeName,
            OrganizationType type,
            EthiopianAddress registeredAddress,
            EthiopianPhoneNumber phone,
            String email,
            TenantId registeringTenantId,
            String ownerCitizenId,
            LocalDate establishedOn,
            LocalDate today,
            Instant now) {
        Objects.requireNonNull(legalName, "legalName");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(registeredAddress, "registeredAddress");
        Objects.requireNonNull(phone, "phone");
        Objects.requireNonNull(registeringTenantId, "registeringTenantId");
        validateEstablishedOn(establishedOn, today);
        String ownerId = null;
        if (type == OrganizationType.SOLE_PROPRIETORSHIP) {
            ownerId = requireCitizenId(ownerCitizenId);
        } else if (ownerCitizenId != null && !ownerCitizenId.isBlank()) {
            ownerId = requireCitizenId(ownerCitizenId);
        }
        Organization organization = new Organization(
                OrganizationId.generate(),
                legalName,
                normalizeOptionalText(tradeName, "tradeName", 200),
                type,
                OrganizationStatus.ACTIVE,
                null,
                null,
                registeredAddress,
                phone,
                normalizeEmail(email),
                registeringTenantId,
                ownerId,
                establishedOn,
                null,
                now,
                now,
                0);
        organization.record(new OrganizationRegistered(organization.id, registeringTenantId, type, now));
        return organization;
    }

    public static Organization rehydrate(
            OrganizationId id,
            LegalName legalName,
            String tradeName,
            OrganizationType type,
            OrganizationStatus status,
            TinNumber tin,
            String registrationNumber,
            EthiopianAddress registeredAddress,
            EthiopianPhoneNumber phone,
            String email,
            TenantId registeringTenantId,
            String ownerCitizenId,
            LocalDate establishedOn,
            String suspensionReason,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new Organization(
                id,
                legalName,
                tradeName,
                type,
                status,
                tin,
                registrationNumber,
                registeredAddress,
                phone,
                email,
                registeringTenantId,
                ownerCitizenId,
                establishedOn,
                suspensionReason,
                createdAt,
                updatedAt,
                version);
    }

    public void updateProfile(
            LegalName legalName,
            String tradeName,
            EthiopianAddress registeredAddress,
            EthiopianPhoneNumber phone,
            String email,
            String registrationNumber,
            LocalDate establishedOn,
            LocalDate today,
            Instant now) {
        ensureActive();
        Objects.requireNonNull(legalName, "legalName");
        Objects.requireNonNull(registeredAddress, "registeredAddress");
        Objects.requireNonNull(phone, "phone");
        validateEstablishedOn(establishedOn, today);
        this.legalName = legalName;
        this.tradeName = normalizeOptionalText(tradeName, "tradeName", 200);
        this.registeredAddress = registeredAddress;
        this.phone = phone;
        this.email = normalizeEmail(email);
        this.registrationNumber = normalizeOptionalText(registrationNumber, "registrationNumber", 64);
        this.establishedOn = establishedOn;
        touch(now);
        record(new OrganizationProfileUpdated(id, now));
    }

    public void assignTin(TinNumber tin, Instant now) {
        ensureActive();
        Objects.requireNonNull(tin, "tin");
        if (this.tin != null) {
            if (this.tin.equals(tin)) {
                return;
            }
            throw new DomainException(
                    "TIN_ALREADY_ASSIGNED",
                    "TIN Already Assigned",
                    "Organization already has a TIN; corrections require a controlled amendment",
                    409);
        }
        this.tin = tin;
        touch(now);
        record(new OrganizationTinAssigned(id, tin, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (status == OrganizationStatus.DISSOLVED || status == OrganizationStatus.ARCHIVED) {
            throw new DomainException(
                    "ORGANIZATION_TERMINAL",
                    "Organization Terminal",
                    "Dissolved or archived organizations cannot be suspended",
                    409);
        }
        if (status == OrganizationStatus.SUSPENDED) {
            return;
        }
        String normalized = reason.trim();
        if (normalized.length() < 5 || normalized.length() > 500) {
            throw new IllegalArgumentException("suspension reason length must be between 5 and 500");
        }
        this.status = OrganizationStatus.SUSPENDED;
        this.suspensionReason = normalized;
        touch(now);
        record(new OrganizationSuspended(id, normalized, now));
    }

    public void reinstate(Instant now) {
        if (status != OrganizationStatus.SUSPENDED) {
            throw new DomainException(
                    "ORGANIZATION_NOT_SUSPENDED",
                    "Organization Not Suspended",
                    "Only suspended organizations can be reinstated",
                    409);
        }
        this.status = OrganizationStatus.ACTIVE;
        this.suspensionReason = null;
        touch(now);
        record(new OrganizationReinstated(id, now));
    }

    public void dissolve(Instant now) {
        if (status == OrganizationStatus.DISSOLVED) {
            return;
        }
        if (status == OrganizationStatus.ARCHIVED) {
            throw new DomainException(
                    "ORGANIZATION_ARCHIVED",
                    "Organization Archived",
                    "Archived organizations cannot be dissolved",
                    409);
        }
        this.status = OrganizationStatus.DISSOLVED;
        this.suspensionReason = null;
        touch(now);
        record(new OrganizationDissolved(id, now));
    }

    public List<OrganizationDomainEvent> pullDomainEvents() {
        List<OrganizationDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureActive() {
        if (status == OrganizationStatus.SUSPENDED) {
            throw new DomainException(
                    "ORGANIZATION_SUSPENDED",
                    "Organization Suspended",
                    "Suspended organizations cannot be modified",
                    409);
        }
        if (status == OrganizationStatus.DISSOLVED) {
            throw new DomainException(
                    "ORGANIZATION_DISSOLVED",
                    "Organization Dissolved",
                    "Dissolved organizations cannot be modified",
                    409);
        }
        if (status == OrganizationStatus.ARCHIVED) {
            throw new DomainException(
                    "ORGANIZATION_ARCHIVED",
                    "Organization Archived",
                    "Archived organizations cannot be modified",
                    409);
        }
    }

    private static void validateEstablishedOn(LocalDate establishedOn, LocalDate today) {
        if (establishedOn == null) {
            return;
        }
        if (establishedOn.isAfter(today)) {
            throw new IllegalArgumentException("establishedOn cannot be in the future");
        }
        if (establishedOn.isBefore(LocalDate.of(1800, 1, 1))) {
            throw new IllegalArgumentException("establishedOn is unrealistically old");
        }
    }

    private static String requireCitizenId(String citizenId) {
        Objects.requireNonNull(citizenId, "ownerCitizenId");
        String normalized = citizenId.trim();
        if (!normalized.startsWith("cit_") || normalized.length() < 40) {
            throw new IllegalArgumentException("ownerCitizenId must be a CitizenId");
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

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (normalized.length() > 128) {
            throw new IllegalArgumentException("email too long");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(OrganizationDomainEvent event) {
        domainEvents.add(event);
    }

    public OrganizationId id() {
        return id;
    }

    public LegalName legalName() {
        return legalName;
    }

    public Optional<String> tradeName() {
        return Optional.ofNullable(tradeName);
    }

    public OrganizationType type() {
        return type;
    }

    public OrganizationStatus status() {
        return status;
    }

    public Optional<TinNumber> tin() {
        return Optional.ofNullable(tin);
    }

    public Optional<String> registrationNumber() {
        return Optional.ofNullable(registrationNumber);
    }

    public EthiopianAddress registeredAddress() {
        return registeredAddress;
    }

    public EthiopianPhoneNumber phone() {
        return phone;
    }

    public Optional<String> email() {
        return Optional.ofNullable(email);
    }

    public TenantId registeringTenantId() {
        return registeringTenantId;
    }

    public Optional<String> ownerCitizenId() {
        return Optional.ofNullable(ownerCitizenId);
    }

    public Optional<LocalDate> establishedOn() {
        return Optional.ofNullable(establishedOn);
    }

    public Optional<String> suspensionReason() {
        return Optional.ofNullable(suspensionReason);
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
