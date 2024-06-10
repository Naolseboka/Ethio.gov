package et.gov.ethgov.permits.domain.permit;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class ConstructionPermit {
    private static final int MAX_DOCUMENTS = 50;
    private static final int MAX_FLOORS = 200;

    private final PermitId id;
    private final TenantId tenantId;
    private final PermitNumber permitNumber;
    private PermitType type;
    private StructureUse structureUse;
    private String projectName;
    private String description;
    private PermitStatus status;
    private PermitApplicant applicant;
    private String parcelId;
    private int floors;
    private FloorAreaSquareMeters floorAreaSquareMeters;
    private Money estimatedCostEtb;
    private String region;
    private String zone;
    private String woreda;
    private String kebele;
    private String locality;
    private String linkedCaseId;
    private String processInstanceId;
    private final Set<String> documentIds;
    private Instant validFrom;
    private Instant validTo;
    private Instant issuedAt;
    private Instant completedAt;
    private String rejectionReason;
    private String suspensionReason;
    private String revocationReason;
    private Instant appliedAt;
    private Instant updatedAt;
    private long version;
    private final List<PermitDomainEvent> domainEvents = new ArrayList<>();

    private ConstructionPermit(
            PermitId id,
            TenantId tenantId,
            PermitNumber permitNumber,
            PermitType type,
            StructureUse structureUse,
            String projectName,
            String description,
            PermitStatus status,
            PermitApplicant applicant,
            String parcelId,
            int floors,
            FloorAreaSquareMeters floorAreaSquareMeters,
            Money estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            Instant validFrom,
            Instant validTo,
            Instant issuedAt,
            Instant completedAt,
            String rejectionReason,
            String suspensionReason,
            String revocationReason,
            Instant appliedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.permitNumber = permitNumber;
        this.type = type;
        this.structureUse = structureUse;
        this.projectName = projectName;
        this.description = description;
        this.status = status;
        this.applicant = applicant;
        this.parcelId = parcelId;
        this.floors = floors;
        this.floorAreaSquareMeters = floorAreaSquareMeters;
        this.estimatedCostEtb = estimatedCostEtb;
        this.region = region;
        this.zone = zone;
        this.woreda = woreda;
        this.kebele = kebele;
        this.locality = locality;
        this.linkedCaseId = linkedCaseId;
        this.processInstanceId = processInstanceId;
        this.documentIds = new LinkedHashSet<>(documentIds);
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuedAt = issuedAt;
        this.completedAt = completedAt;
        this.rejectionReason = rejectionReason;
        this.suspensionReason = suspensionReason;
        this.revocationReason = revocationReason;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ConstructionPermit apply(
            TenantId tenantId,
            PermitNumber permitNumber,
            PermitType type,
            StructureUse structureUse,
            String projectName,
            String description,
            PermitApplicant applicant,
            String parcelId,
            int floors,
            FloorAreaSquareMeters floorAreaSquareMeters,
            Money estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String linkedCaseId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(permitNumber, "permitNumber");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(structureUse, "structureUse");
        Objects.requireNonNull(applicant, "applicant");
        Objects.requireNonNull(floorAreaSquareMeters, "floorAreaSquareMeters");
        Objects.requireNonNull(now, "now");
        requireNonBlank(projectName, "projectName");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        requireParcelId(parcelId);
        requireFloors(floors);
        validateOptionalId(linkedCaseId, "cas_", "linkedCaseId");
        if (estimatedCostEtb != null && estimatedCostEtb.isNegative()) {
            throw new IllegalArgumentException("estimatedCostEtb must not be negative");
        }
        ConstructionPermit permit = new ConstructionPermit(
                PermitId.generate(),
                tenantId,
                permitNumber,
                type,
                structureUse,
                projectName.trim(),
                blankToNull(description),
                PermitStatus.APPLIED,
                applicant,
                parcelId.trim(),
                floors,
                floorAreaSquareMeters,
                estimatedCostEtb,
                region.trim(),
                blankToNull(zone),
                woreda.trim(),
                blankToNull(kebele),
                blankToNull(locality),
                blankToNull(linkedCaseId),
                null,
                Set.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                0L);
        permit.record(new PermitApplied(permit.id, tenantId, permitNumber, type, now));
        return permit;
    }

    public static ConstructionPermit rehydrate(
            PermitId id,
            TenantId tenantId,
            PermitNumber permitNumber,
            PermitType type,
            StructureUse structureUse,
            String projectName,
            String description,
            PermitStatus status,
            PermitApplicant applicant,
            String parcelId,
            int floors,
            FloorAreaSquareMeters floorAreaSquareMeters,
            Money estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String linkedCaseId,
            String processInstanceId,
            Set<String> documentIds,
            Instant validFrom,
            Instant validTo,
            Instant issuedAt,
            Instant completedAt,
            String rejectionReason,
            String suspensionReason,
            String revocationReason,
            Instant appliedAt,
            Instant updatedAt,
            long version) {
        return new ConstructionPermit(
                id, tenantId, permitNumber, type, structureUse, projectName, description, status,
                applicant, parcelId, floors, floorAreaSquareMeters, estimatedCostEtb,
                region, zone, woreda, kebele, locality, linkedCaseId, processInstanceId,
                documentIds == null ? Set.of() : documentIds,
                validFrom, validTo, issuedAt, completedAt,
                rejectionReason, suspensionReason, revocationReason,
                appliedAt, updatedAt, version);
    }

    public void updateApplication(
            String projectName,
            String description,
            StructureUse structureUse,
            int floors,
            FloorAreaSquareMeters floorAreaSquareMeters,
            Money estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            Instant now) {
        if (status != PermitStatus.APPLIED && status != PermitStatus.UNDER_REVIEW) {
            throw new DomainException(
                    "PERMIT_NOT_EDITABLE",
                    "Permit Not Editable",
                    "Only APPLIED or UNDER_REVIEW permits can be updated",
                    409);
        }
        Objects.requireNonNull(structureUse, "structureUse");
        Objects.requireNonNull(floorAreaSquareMeters, "floorAreaSquareMeters");
        Objects.requireNonNull(now, "now");
        requireNonBlank(projectName, "projectName");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        requireFloors(floors);
        if (estimatedCostEtb != null && estimatedCostEtb.isNegative()) {
            throw new IllegalArgumentException("estimatedCostEtb must not be negative");
        }
        this.projectName = projectName.trim();
        this.description = blankToNull(description);
        this.structureUse = structureUse;
        this.floors = floors;
        this.floorAreaSquareMeters = floorAreaSquareMeters;
        this.estimatedCostEtb = estimatedCostEtb;
        this.region = region.trim();
        this.zone = blankToNull(zone);
        this.woreda = woreda.trim();
        this.kebele = blankToNull(kebele);
        this.locality = blankToNull(locality);
        this.updatedAt = now;
        record(new PermitUpdated(id, now));
    }

    public void startReview(Instant now) {
        Objects.requireNonNull(now, "now");
        requireStatus(PermitStatus.APPLIED, "start review");
        this.status = PermitStatus.UNDER_REVIEW;
        this.updatedAt = now;
        record(new PermitReviewStarted(id, now));
    }

    public void issue(Instant validFrom, Instant validTo, Instant now) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(validFrom, "validFrom");
        Objects.requireNonNull(validTo, "validTo");
        requireStatus(PermitStatus.UNDER_REVIEW, "issue");
        if (!validTo.isAfter(validFrom)) {
            throw new IllegalArgumentException("validTo must be after validFrom");
        }
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.issuedAt = now;
        this.status = PermitStatus.ISSUED;
        this.updatedAt = now;
        record(new PermitIssued(id, now));
    }

    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        requireStatus(PermitStatus.ISSUED, "activate");
        this.status = PermitStatus.ACTIVE;
        this.updatedAt = now;
        record(new PermitActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        requireStatus(PermitStatus.ACTIVE, "suspend");
        this.status = PermitStatus.SUSPENDED;
        this.suspensionReason = reason.trim();
        this.updatedAt = now;
        record(new PermitSuspended(id, this.suspensionReason, now));
    }

    public void reinstate(Instant now) {
        Objects.requireNonNull(now, "now");
        requireStatus(PermitStatus.SUSPENDED, "reinstate");
        this.status = PermitStatus.ACTIVE;
        this.suspensionReason = null;
        this.updatedAt = now;
        record(new PermitReinstated(id, now));
    }

    public void complete(Instant now) {
        Objects.requireNonNull(now, "now");
        requireStatus(PermitStatus.ACTIVE, "complete");
        this.status = PermitStatus.COMPLETED;
        this.completedAt = now;
        this.updatedAt = now;
        record(new PermitCompleted(id, now));
    }

    public void revoke(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != PermitStatus.ACTIVE && status != PermitStatus.SUSPENDED) {
            throw invalidState("revoke");
        }
        this.status = PermitStatus.REVOKED;
        this.revocationReason = reason.trim();
        this.updatedAt = now;
        record(new PermitRevoked(id, this.revocationReason, now));
    }

    public void reject(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != PermitStatus.UNDER_REVIEW && status != PermitStatus.APPLIED) {
            throw invalidState("reject");
        }
        this.status = PermitStatus.REJECTED;
        this.rejectionReason = reason.trim();
        this.updatedAt = now;
        record(new PermitRejected(id, this.rejectionReason, now));
    }

    public void expire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != PermitStatus.ACTIVE && status != PermitStatus.SUSPENDED && status != PermitStatus.ISSUED) {
            throw invalidState("expire");
        }
        this.status = PermitStatus.EXPIRED;
        this.updatedAt = now;
        record(new PermitExpired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == PermitStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason, "reason");
        if (status != PermitStatus.APPLIED && status != PermitStatus.UNDER_REVIEW) {
            throw invalidState("cancel");
        }
        this.status = PermitStatus.CANCELLED;
        this.updatedAt = now;
        record(new PermitCancelled(id, reason.trim(), now));
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
                    "Permit cannot attach more than " + MAX_DOCUMENTS + " documents",
                    409);
        }
        documentIds.add(doc);
        this.updatedAt = now;
        record(new PermitDocumentAttached(id, doc, now));
    }

    public void linkCase(String caseId, Instant now) {
        assertNotTerminal();
        Objects.requireNonNull(now, "now");
        validateOptionalId(caseId, "cas_", "linkedCaseId");
        requireNonBlank(caseId, "linkedCaseId");
        this.linkedCaseId = caseId.trim();
        this.updatedAt = now;
        record(new PermitUpdated(id, now));
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
                    "Permit already linked to a process instance",
                    409);
        }
        this.processInstanceId = processInstanceId.trim();
        this.updatedAt = now;
        record(new PermitUpdated(id, now));
    }

    private void requireStatus(PermitStatus expected, String action) {
        if (status != expected) {
            throw invalidState(action);
        }
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_PERMIT_STATE",
                "Invalid Permit State",
                "Cannot " + action + " permit in status " + status,
                409);
    }

    private void assertNotTerminal() {
        if (status == PermitStatus.REVOKED
                || status == PermitStatus.REJECTED
                || status == PermitStatus.CANCELLED
                || status == PermitStatus.COMPLETED) {
            throw new DomainException(
                    "PERMIT_TERMINAL",
                    "Permit Terminal",
                    "Permit is terminal and cannot be modified",
                    409);
        }
    }

    private static void requireFloors(int floors) {
        if (floors < 1 || floors > MAX_FLOORS) {
            throw new IllegalArgumentException("floors must be between 1 and " + MAX_FLOORS);
        }
    }

    private static void requireParcelId(String parcelId) {
        requireNonBlank(parcelId, "parcelId");
        String n = parcelId.trim();
        if (!n.startsWith("par_") || n.length() < 40) {
            throw new IllegalArgumentException("parcelId must be a valid par_ identifier");
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
        if (value == null || value.isBlank()) return;
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

    private void record(PermitDomainEvent event) {
        domainEvents.add(event);
    }

    public List<PermitDomainEvent> pullDomainEvents() {
        List<PermitDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public PermitId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public PermitNumber permitNumber() { return permitNumber; }
    public PermitType type() { return type; }
    public StructureUse structureUse() { return structureUse; }
    public String projectName() { return projectName; }
    public Optional<String> description() { return Optional.ofNullable(description); }
    public PermitStatus status() { return status; }
    public PermitApplicant applicant() { return applicant; }
    public String parcelId() { return parcelId; }
    public int floors() { return floors; }
    public FloorAreaSquareMeters floorAreaSquareMeters() { return floorAreaSquareMeters; }
    public Optional<Money> estimatedCostEtb() { return Optional.ofNullable(estimatedCostEtb); }
    public String region() { return region; }
    public Optional<String> zone() { return Optional.ofNullable(zone); }
    public String woreda() { return woreda; }
    public Optional<String> kebele() { return Optional.ofNullable(kebele); }
    public Optional<String> locality() { return Optional.ofNullable(locality); }
    public Optional<String> linkedCaseId() { return Optional.ofNullable(linkedCaseId); }
    public Optional<String> processInstanceId() { return Optional.ofNullable(processInstanceId); }
    public Set<String> documentIds() { return Collections.unmodifiableSet(documentIds); }
    public Optional<Instant> validFrom() { return Optional.ofNullable(validFrom); }
    public Optional<Instant> validTo() { return Optional.ofNullable(validTo); }
    public Optional<Instant> issuedAt() { return Optional.ofNullable(issuedAt); }
    public Optional<Instant> completedAt() { return Optional.ofNullable(completedAt); }
    public Optional<String> rejectionReason() { return Optional.ofNullable(rejectionReason); }
    public Optional<String> suspensionReason() { return Optional.ofNullable(suspensionReason); }
    public Optional<String> revocationReason() { return Optional.ofNullable(revocationReason); }
    public Instant appliedAt() { return appliedAt; }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
