package et.gov.ethgov.directory.domain.employee;

import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Public employee directory entry for a government tenant (not a full HRIS payroll system).
 */
public final class PublicEmployee {

    private final PublicEmployeeId id;
    private final TenantId tenantId;
    private final EmployeeNumber employeeNumber;
    private final String citizenId;
    private String displayName;
    private OrganizationalUnitId organizationalUnitId;
    private String jobTitle;
    private String identityAccountId;
    private PublicEmployeeStatus status;
    private final LocalDate hiredOn;
    private LocalDate separatedOn;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<PublicEmployeeDomainEvent> domainEvents = new ArrayList<>();

    private PublicEmployee(
            PublicEmployeeId id,
            TenantId tenantId,
            EmployeeNumber employeeNumber,
            String citizenId,
            String displayName,
            OrganizationalUnitId organizationalUnitId,
            String jobTitle,
            String identityAccountId,
            PublicEmployeeStatus status,
            LocalDate hiredOn,
            LocalDate separatedOn,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeNumber = employeeNumber;
        this.citizenId = citizenId;
        this.displayName = displayName;
        this.organizationalUnitId = organizationalUnitId;
        this.jobTitle = jobTitle;
        this.identityAccountId = identityAccountId;
        this.status = status;
        this.hiredOn = hiredOn;
        this.separatedOn = separatedOn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static PublicEmployee register(
            TenantId tenantId,
            EmployeeNumber employeeNumber,
            String citizenId,
            String displayName,
            OrganizationalUnitId organizationalUnitId,
            String jobTitle,
            LocalDate hiredOn,
            LocalDate today,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(employeeNumber, "employeeNumber");
        Objects.requireNonNull(organizationalUnitId, "organizationalUnitId");
        Objects.requireNonNull(hiredOn, "hiredOn");
        if (hiredOn.isAfter(today)) {
            throw new IllegalArgumentException("hiredOn cannot be in the future");
        }
        PublicEmployee employee = new PublicEmployee(
                PublicEmployeeId.generate(),
                tenantId,
                employeeNumber,
                requireCitizenId(citizenId),
                requireDisplayName(displayName),
                organizationalUnitId,
                requireJobTitle(jobTitle),
                null,
                PublicEmployeeStatus.ACTIVE,
                hiredOn,
                null,
                now,
                now,
                0);
        employee.record(new PublicEmployeeRegistered(
                employee.id, tenantId, organizationalUnitId, employeeNumber, now));
        return employee;
    }

    public static PublicEmployee rehydrate(
            PublicEmployeeId id,
            TenantId tenantId,
            EmployeeNumber employeeNumber,
            String citizenId,
            String displayName,
            OrganizationalUnitId organizationalUnitId,
            String jobTitle,
            String identityAccountId,
            PublicEmployeeStatus status,
            LocalDate hiredOn,
            LocalDate separatedOn,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new PublicEmployee(
                id,
                tenantId,
                employeeNumber,
                citizenId,
                displayName,
                organizationalUnitId,
                jobTitle,
                identityAccountId,
                status,
                hiredOn,
                separatedOn,
                createdAt,
                updatedAt,
                version);
    }

    public void changeAssignment(OrganizationalUnitId organizationalUnitId, String jobTitle, Instant now) {
        ensureActive();
        Objects.requireNonNull(organizationalUnitId, "organizationalUnitId");
        String title = requireJobTitle(jobTitle);
        if (this.organizationalUnitId.equals(organizationalUnitId) && this.jobTitle.equals(title)) {
            return;
        }
        this.organizationalUnitId = organizationalUnitId;
        this.jobTitle = title;
        touch(now);
        record(new PublicEmployeeAssignmentChanged(id, organizationalUnitId, title, now));
    }

    public void updateDisplayName(String displayName, Instant now) {
        ensureActive();
        this.displayName = requireDisplayName(displayName);
        touch(now);
    }

    public void linkIdentityAccount(String identityAccountId, Instant now) {
        ensureActive();
        String accountId = requireUserId(identityAccountId);
        if (this.identityAccountId != null) {
            if (this.identityAccountId.equals(accountId)) {
                return;
            }
            throw new DomainException(
                    "IDENTITY_ALREADY_LINKED",
                    "Identity Already Linked",
                    "Public employee already linked to an identity account",
                    409);
        }
        this.identityAccountId = accountId;
        touch(now);
        record(new PublicEmployeeIdentityLinked(id, accountId, now));
    }

    public void separate(LocalDate separatedOn, LocalDate today, Instant now) {
        Objects.requireNonNull(separatedOn, "separatedOn");
        if (status == PublicEmployeeStatus.SEPARATED) {
            return;
        }
        if (separatedOn.isAfter(today)) {
            throw new IllegalArgumentException("separatedOn cannot be in the future");
        }
        if (separatedOn.isBefore(hiredOn)) {
            throw new IllegalArgumentException("separatedOn cannot be before hiredOn");
        }
        this.status = PublicEmployeeStatus.SEPARATED;
        this.separatedOn = separatedOn;
        touch(now);
        record(new PublicEmployeeSeparated(id, separatedOn, now));
    }

    public List<PublicEmployeeDomainEvent> pullDomainEvents() {
        List<PublicEmployeeDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureActive() {
        if (status == PublicEmployeeStatus.SEPARATED) {
            throw new DomainException(
                    "EMPLOYEE_SEPARATED",
                    "Employee Separated",
                    "Separated employees cannot be modified",
                    409);
        }
    }

    private static String requireCitizenId(String citizenId) {
        Objects.requireNonNull(citizenId, "citizenId");
        String normalized = citizenId.trim();
        if (!normalized.startsWith("cit_") || normalized.length() < 40) {
            throw new IllegalArgumentException("citizenId must be a CitizenId");
        }
        return normalized;
    }

    private static String requireUserId(String userId) {
        Objects.requireNonNull(userId, "identityAccountId");
        String normalized = userId.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("identityAccountId must be a UserAccountId");
        }
        return normalized;
    }

    private static String requireDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "displayName");
        String normalized = displayName.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("displayName length must be between 2 and 200");
        }
        return normalized;
    }

    private static String requireJobTitle(String jobTitle) {
        Objects.requireNonNull(jobTitle, "jobTitle");
        String normalized = jobTitle.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 120) {
            throw new IllegalArgumentException("jobTitle length must be between 2 and 120");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(PublicEmployeeDomainEvent event) {
        domainEvents.add(event);
    }

    public PublicEmployeeId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public EmployeeNumber employeeNumber() {
        return employeeNumber;
    }

    public String citizenId() {
        return citizenId;
    }

    public String displayName() {
        return displayName;
    }

    public OrganizationalUnitId organizationalUnitId() {
        return organizationalUnitId;
    }

    public String jobTitle() {
        return jobTitle;
    }

    public Optional<String> identityAccountId() {
        return Optional.ofNullable(identityAccountId);
    }

    public PublicEmployeeStatus status() {
        return status;
    }

    public LocalDate hiredOn() {
        return hiredOn;
    }

    public Optional<LocalDate> separatedOn() {
        return Optional.ofNullable(separatedOn);
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
