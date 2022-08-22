package et.gov.ethgov.licensing.domain.license;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class BusinessLicense {
    private static final int MAX_DOCUMENTS = 50;

    private final LicenseId id;
    private final TenantId tenantId;
    private final LicenseNumber licenseNumber;
    private LicenseType type;
    private String businessName;
    private String activityDescription;
    private LicenseStatus status;
    private LicenseHolder holder;
    private String region;
    private String zone;
    private String woreda;
    private String kebele;
    private String locality;
    private String parcelId;
    private String linkedCaseId;
    private String processInstanceId;
    private final Set<String> documentIds;
    private Instant validFrom;
    private Instant validTo;
    private Instant issuedAt;
    private String rejectionReason;
    private String suspensionReason;
    private String revocationReason;
    private Instant appliedAt;
    private Instant updatedAt;
    private long version;
    private final List<LicenseDomainEvent> domainEvents = new ArrayList<>();

    private BusinessLicense(
            LicenseId id,
            TenantId tenantId,
            LicenseNumber licenseNumber,
            LicenseType type,
            String businessName,
            String activityDescription,
            LicenseStatus status,
            LicenseHolder holder,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            Instant validFrom,
            Instant validTo,
            Instant issuedAt,
            String rejectionReason,
            String suspensionReason,
            String revocationReason,
            Instant appliedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.licenseNumber = licenseNumber;
        this.type = type;
        this.businessName = businessName;
        this.activityDescription = activityDescription;
        this.status = status;
        this.holder = holder;
        this.region = region;
        this.zone = zone;
        this.woreda = woreda;
        this.kebele = kebele;
        this.locality = locality;
        this.parcelId = parcelId;
        this.linkedCaseId = linkedCaseId;
        this.processInstanceId = processInstanceId;
        this.documentIds = new LinkedHashSet<>(documentIds);
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuedAt = issuedAt;
        this.rejectionReason = rejectionReason;
        this.suspensionReason = suspensionReason;
        this.revocationReason = revocationReason;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static BusinessLicense apply(
            TenantId tenantId,
            LicenseNumber licenseNumber,
            LicenseType type,
            String businessName,
            String activityDescription,
            LicenseHolder holder,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(licenseNumber, "licenseNumber");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(holder, "holder");
        Objects.requireNonNull(now, "now");
        requireNonBlank(businessName, "businessName");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        validateOptionalId(parcelId, "par_", "parcelId");
        validateOptionalId(linkedCaseId, "cas_", "linkedCaseId");
        BusinessLicense license = new BusinessLicense(
                LicenseId.generate(),
                tenantId,
                licenseNumber,
                type,
                businessName.trim(),
                blankToNull(activityDescription),
                LicenseStatus.APPLIED,
                holder,
                region.trim(),
                blankToNull(zone),
                woreda.trim(),
                blankToNull(kebele),
                blankToNull(locality),
                blankToNull(parcelId),
                blankToNull(linkedCaseId),
                null,
                Set.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                0L);
        license.record(new LicenseApplied(license.id, tenantId, licenseNumber, type, now));
        return license;
    }

    public static BusinessLicense rehydrate(
            LicenseId id,
            TenantId tenantId,
            LicenseNumber licenseNumber,
            LicenseType type,
            String businessName,
            String activityDescription,
            LicenseStatus status,
            LicenseHolder holder,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            Instant validFrom,
            Instant validTo,
            Instant issuedAt,
            String rejectionReason,
            String suspensionReason,
            String revocationReason,
            Instant appliedAt,
            Instant updatedAt,
            long version) {
        return new BusinessLicense(
                id, tenantId, licenseNumber, type, businessName, activityDescription, status, holder,
                region, zone, woreda, kebele, locality, parcelId, linkedCaseId, processInstanceId,
                documentIds == null ? Set.of() : documentIds,
                validFrom, validTo, issuedAt, rejectionReason, suspensionReason, revocationReason,
                appliedAt, updatedAt, version);
    }

    public void updateApplication(
            String businessName,
            String activityDescription,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            Instant now) {
        if (status != LicenseStatus.APPLIED && status != LicenseStatus.UNDER_REVIEW) {
            throw new DomainException(
                    "LICENSE_NOT_EDITABLE",
                    "License Not Editable",
                    "Only APPLIED or UNDER_REVIEW licenses can be updated",
                    409);
        }
        Objects.requireNonNull(now, "now");
        requireNonBlank(businessName, "businessName");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        validateOptionalId(parcelId, "par_", "parcelId");
        this.businessName = businessName.trim();
        this.activityDescription = blankToNull(activityDescription);
        this.region = region.trim();
        this.zone = blankToNull(zone);
        this.woreda = woreda.trim();
        this.kebele = blankToNull(kebele);
        this.locality = blankToNull(locality);
        this.parcelId = blankToNull(parcelId);
        this.updatedAt = now;
        record(new LicenseUpdated(id, now));
    }

    public void startReview(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != LicenseStatus.APPLIED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be APPLIED to start review; was " + status,
                    409);
        }
        this.status = LicenseStatus.UNDER_REVIEW;
        this.updatedAt = now;
        record(new LicenseReviewStarted(id, now));
    }

    public void issue(Instant validFrom, Instant validTo, Instant now) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(validFrom, "validFrom");
        Objects.requireNonNull(validTo, "validTo");
        if (status != LicenseStatus.UNDER_REVIEW) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be UNDER_REVIEW to issue; was " + status,
                    409);
        }
        if (!validTo.isAfter(validFrom)) {
            throw new IllegalArgumentException("validTo must be after validFrom");
        }
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuedAt = now;
        this.status = LicenseStatus.ISSUED;
        this.updatedAt = now;
        record(new LicenseIssued(id, now));
    }

    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != LicenseStatus.ISSUED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be ISSUED to activate; was " + status,
                    409);
        }
        this.status = LicenseStatus.ACTIVE;
        this.updatedAt = now;
        record(new LicenseActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != LicenseStatus.ACTIVE) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be ACTIVE to suspend; was " + status,
                    409);
        }
        this.status = LicenseStatus.SUSPENDED;
        this.suspensionReason = reason.trim();
        this.updatedAt = now;
        record(new LicenseSuspended(id, this.suspensionReason, now));
    }

    public void reinstate(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != LicenseStatus.SUSPENDED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be SUSPENDED to reinstate; was " + status,
                    409);
        }
        this.status = LicenseStatus.ACTIVE;
        this.suspensionReason = null;
        this.updatedAt = now;
        record(new LicenseReinstated(id, now));
    }

    public void revoke(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != LicenseStatus.ACTIVE && status != LicenseStatus.SUSPENDED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be ACTIVE or SUSPENDED to revoke; was " + status,
                    409);
        }
        this.status = LicenseStatus.REVOKED;
        this.revocationReason = reason.trim();
        this.updatedAt = now;
        record(new LicenseRevoked(id, this.revocationReason, now));
    }

    public void reject(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != LicenseStatus.UNDER_REVIEW && status != LicenseStatus.APPLIED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be APPLIED or UNDER_REVIEW to reject; was " + status,
                    409);
        }
        this.status = LicenseStatus.REJECTED;
        this.rejectionReason = reason.trim();
        this.updatedAt = now;
        record(new LicenseRejected(id, this.rejectionReason, now));
    }

    public void renew(Instant newValidTo, Instant now) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(newValidTo, "newValidTo");
        if (status != LicenseStatus.ACTIVE && status != LicenseStatus.EXPIRED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be ACTIVE or EXPIRED to renew; was " + status,
                    409);
        }
        if (validTo != null && !newValidTo.isAfter(validTo)) {
            throw new IllegalArgumentException("newValidTo must be after current validTo");
        }
        this.validTo = newValidTo;
        if (status == LicenseStatus.EXPIRED) {
            this.status = LicenseStatus.ACTIVE;
        }
        this.updatedAt = now;
        record(new LicenseRenewed(id, newValidTo, now));
    }

    public void expire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != LicenseStatus.ACTIVE && status != LicenseStatus.SUSPENDED) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "License must be ACTIVE or SUSPENDED to expire; was " + status,
                    409);
        }
        this.status = LicenseStatus.EXPIRED;
        this.updatedAt = now;
        record(new LicenseExpired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == LicenseStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != LicenseStatus.APPLIED && status != LicenseStatus.UNDER_REVIEW) {
            throw new DomainException(
                    "INVALID_LICENSE_STATE",
                    "Invalid License State",
                    "Only APPLIED or UNDER_REVIEW licenses can be cancelled; was " + status,
                    409);
        }
        this.status = LicenseStatus.CANCELLED;
        this.updatedAt = now;
        record(new LicenseCancelled(id, reason.trim(), now));
    }

    public void attachDocument(String documentId, Instant now) {
        assertNotTerminal();
        Objects.requireNonNull(now, "now");
        requireDocId(documentId);
        String doc = documentId.trim();
        if (documentIds.contains(doc)) {
            return;
        }
        if (documentIds.size() >= MAX_DOCUMENTS) {
            throw new DomainException(
                    "TOO_MANY_DOCUMENTS",
                    "Too Many Documents",
                    "License cannot attach more than " + MAX_DOCUMENTS + " documents",
                    409);
        }
        documentIds.add(doc);
        this.updatedAt = now;
        record(new LicenseDocumentAttached(id, doc, now));
    }

    public void linkCase(String caseId, Instant now) {
        assertNotTerminal();
        Objects.requireNonNull(now, "now");
        validateOptionalId(caseId, "cas_", "linkedCaseId");
        requireNonBlank(caseId, "linkedCaseId");
        this.linkedCaseId = caseId.trim();
        this.updatedAt = now;
        record(new LicenseUpdated(id, now));
    }

    public void linkWorkflow(String processInstanceId, Instant now) {
        assertNotTerminal();
        Objects.requireNonNull(now, "now");
        validateOptionalId(processInstanceId, "wpi_", "processInstanceId");
        requireNonBlank(processInstanceId, "processInstanceId");
        if (this.processInstanceId != null && !this.processInstanceId.equals(processInstanceId.trim())) {
            throw new DomainException(
                    "WORKFLOW_ALREADY_LINKED",
                    "Workflow Already Linked",
                    "License already linked to a process instance",
                    409);
        }
        this.processInstanceId = processInstanceId.trim();
        this.updatedAt = now;
        record(new LicenseUpdated(id, now));
    }

    private void assertNotTerminal() {
        if (status == LicenseStatus.REVOKED
                || status == LicenseStatus.REJECTED
                || status == LicenseStatus.CANCELLED) {
            throw new DomainException(
                    "LICENSE_TERMINAL",
                    "License Terminal",
                    "License is terminal and cannot be modified",
                    409);
        }
    }

    private static void requireReason(String reason, String field) {
        requireNonBlank(reason, field);
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException(field + " length must be between 5 and 500");
        }
    }

    private static void requireDocId(String documentId) {
        requireNonBlank(documentId, "documentId");
        String n = documentId.trim();
        if (!n.startsWith("doc_") || n.length() < 40) {
            throw new IllegalArgumentException("documentId must be a valid doc_ identifier");
        }
    }

    private static void validateOptionalId(String value, String prefix, String field) {
        if (value == null || value.isBlank()) {
            return;
        }
        String n = value.trim();
        if (!n.startsWith(prefix) || n.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
        }
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void record(LicenseDomainEvent event) {
        domainEvents.add(event);
    }

    public List<LicenseDomainEvent> pullDomainEvents() {
        List<LicenseDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public LicenseId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public LicenseNumber licenseNumber() { return licenseNumber; }
    public LicenseType type() { return type; }
    public String businessName() { return businessName; }
    public Optional<String> activityDescription() { return Optional.ofNullable(activityDescription); }
    public LicenseStatus status() { return status; }
    public LicenseHolder holder() { return holder; }
    public String region() { return region; }
    public Optional<String> zone() { return Optional.ofNullable(zone); }
    public String woreda() { return woreda; }
    public Optional<String> kebele() { return Optional.ofNullable(kebele); }
    public Optional<String> locality() { return Optional.ofNullable(locality); }
    public Optional<String> parcelId() { return Optional.ofNullable(parcelId); }
    public Optional<String> linkedCaseId() { return Optional.ofNullable(linkedCaseId); }
    public Optional<String> processInstanceId() { return Optional.ofNullable(processInstanceId); }
    public Set<String> documentIds() { return Collections.unmodifiableSet(documentIds); }
    public Optional<Instant> validFrom() { return Optional.ofNullable(validFrom); }
    public Optional<Instant> validTo() { return Optional.ofNullable(validTo); }
    public Optional<Instant> issuedAt() { return Optional.ofNullable(issuedAt); }
    public Optional<String> rejectionReason() { return Optional.ofNullable(rejectionReason); }
    public Optional<String> suspensionReason() { return Optional.ofNullable(suspensionReason); }
    public Optional<String> revocationReason() { return Optional.ofNullable(revocationReason); }
    public Instant appliedAt() { return appliedAt; }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
