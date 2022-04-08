package et.gov.ethgov.land.domain.parcel;

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

public final class LandParcel {
    private static final int MAX_DOCUMENTS = 50;

    private final ParcelId id;
    private final TenantId tenantId;
    private final ParcelNumber parcelNumber;
    private String region;
    private String zone;
    private String woreda;
    private String kebele;
    private String locality;
    private LandUse landUse;
    private TenureType tenureType;
    private ParcelStatus status;
    private AreaSquareMeters areaSquareMeters;
    private ParcelOwner owner;
    private String titleDeedNumber;
    private String leaseReference;
    private Instant leaseExpiresAt;
    private String linkedCaseId;
    private final Set<String> documentIds;
    private String encumbranceReason;
    private Instant registeredAt;
    private Instant updatedAt;
    private long version;
    private final List<ParcelDomainEvent> domainEvents = new ArrayList<>();

    private LandParcel(
            ParcelId id,
            TenantId tenantId,
            ParcelNumber parcelNumber,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            LandUse landUse,
            TenureType tenureType,
            ParcelStatus status,
            AreaSquareMeters areaSquareMeters,
            ParcelOwner owner,
            String titleDeedNumber,
            String leaseReference,
            Instant leaseExpiresAt,
            String linkedCaseId,
            Set<String> documentIds,
            String encumbranceReason,
            Instant registeredAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.parcelNumber = parcelNumber;
        this.region = region;
        this.zone = zone;
        this.woreda = woreda;
        this.kebele = kebele;
        this.locality = locality;
        this.landUse = landUse;
        this.tenureType = tenureType;
        this.status = status;
        this.areaSquareMeters = areaSquareMeters;
        this.owner = owner;
        this.titleDeedNumber = titleDeedNumber;
        this.leaseReference = leaseReference;
        this.leaseExpiresAt = leaseExpiresAt;
        this.linkedCaseId = linkedCaseId;
        this.documentIds = new LinkedHashSet<>(documentIds);
        this.encumbranceReason = encumbranceReason;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static LandParcel register(
            TenantId tenantId,
            ParcelNumber parcelNumber,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            LandUse landUse,
            TenureType tenureType,
            AreaSquareMeters areaSquareMeters,
            ParcelOwner owner,
            String titleDeedNumber,
            String leaseReference,
            Instant leaseExpiresAt,
            String linkedCaseId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(parcelNumber, "parcelNumber");
        Objects.requireNonNull(landUse, "landUse");
        Objects.requireNonNull(tenureType, "tenureType");
        Objects.requireNonNull(areaSquareMeters, "areaSquareMeters");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(now, "now");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        validateLease(tenureType, leaseReference);
        validateCaseId(linkedCaseId);
        LandParcel parcel = new LandParcel(
                ParcelId.generate(),
                tenantId,
                parcelNumber,
                region.trim(),
                blankToNull(zone),
                woreda.trim(),
                blankToNull(kebele),
                blankToNull(locality),
                landUse,
                tenureType,
                ParcelStatus.ACTIVE,
                areaSquareMeters,
                owner,
                blankToNull(titleDeedNumber),
                blankToNull(leaseReference),
                leaseExpiresAt,
                blankToNull(linkedCaseId),
                Set.of(),
                null,
                now,
                now,
                0L);
        parcel.record(new ParcelRegistered(parcel.id, tenantId, parcelNumber, landUse, now));
        return parcel;
    }

    public static LandParcel rehydrate(
            ParcelId id,
            TenantId tenantId,
            ParcelNumber parcelNumber,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            LandUse landUse,
            TenureType tenureType,
            ParcelStatus status,
            AreaSquareMeters areaSquareMeters,
            ParcelOwner owner,
            String titleDeedNumber,
            String leaseReference,
            Instant leaseExpiresAt,
            String linkedCaseId,
            Set<String> documentIds,
            String encumbranceReason,
            Instant registeredAt,
            Instant updatedAt,
            long version) {
        return new LandParcel(
                id, tenantId, parcelNumber, region, zone, woreda, kebele, locality,
                landUse, tenureType, status, areaSquareMeters, owner, titleDeedNumber,
                leaseReference, leaseExpiresAt, linkedCaseId,
                documentIds == null ? Set.of() : documentIds,
                encumbranceReason, registeredAt, updatedAt, version);
    }

    public void updateDetails(
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            LandUse landUse,
            TenureType tenureType,
            AreaSquareMeters areaSquareMeters,
            String titleDeedNumber,
            String leaseReference,
            Instant leaseExpiresAt,
            Instant now) {
        assertMutable();
        Objects.requireNonNull(landUse, "landUse");
        Objects.requireNonNull(tenureType, "tenureType");
        Objects.requireNonNull(areaSquareMeters, "areaSquareMeters");
        Objects.requireNonNull(now, "now");
        requireNonBlank(region, "region");
        requireNonBlank(woreda, "woreda");
        validateLease(tenureType, leaseReference);
        this.region = region.trim();
        this.zone = blankToNull(zone);
        this.woreda = woreda.trim();
        this.kebele = blankToNull(kebele);
        this.locality = blankToNull(locality);
        this.landUse = landUse;
        this.tenureType = tenureType;
        this.areaSquareMeters = areaSquareMeters;
        this.titleDeedNumber = blankToNull(titleDeedNumber);
        this.leaseReference = blankToNull(leaseReference);
        this.leaseExpiresAt = leaseExpiresAt;
        this.updatedAt = now;
        record(new ParcelUpdated(id, now));
    }

    public void transfer(ParcelOwner newOwner, Instant now) {
        assertActiveOrEncumbered();
        Objects.requireNonNull(newOwner, "newOwner");
        Objects.requireNonNull(now, "now");
        this.owner = newOwner;
        this.updatedAt = now;
        record(new ParcelTransferred(id, now));
    }

    public void encumber(String reason, Instant now) {
        if (status == ParcelStatus.ENCUMBERED) {
            throw new DomainException(
                    "ALREADY_ENCUMBERED",
                    "Already Encumbered",
                    "Parcel is already encumbered",
                    409);
        }
        assertActiveOnly();
        Objects.requireNonNull(now, "now");
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("encumbrance reason length must be between 5 and 500");
        }
        this.status = ParcelStatus.ENCUMBERED;
        this.encumbranceReason = reason.trim();
        this.updatedAt = now;
        record(new ParcelEncumbered(id, this.encumbranceReason, now));
    }

    public void clearEncumbrance(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ParcelStatus.ENCUMBERED) {
            throw new DomainException(
                    "NOT_ENCUMBERED",
                    "Not Encumbered",
                    "Parcel is not encumbered",
                    409);
        }
        this.status = ParcelStatus.ACTIVE;
        this.encumbranceReason = null;
        this.updatedAt = now;
        record(new ParcelEncumbranceCleared(id, now));
    }

    public void attachDocument(String documentId, Instant now) {
        assertMutable();
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
                    "Parcel cannot attach more than " + MAX_DOCUMENTS + " documents",
                    409);
        }
        documentIds.add(doc);
        this.updatedAt = now;
        record(new ParcelDocumentAttached(id, doc, now));
    }

    public void linkCase(String caseId, Instant now) {
        assertMutable();
        Objects.requireNonNull(now, "now");
        validateCaseId(caseId);
        requireNonBlank(caseId, "linkedCaseId");
        this.linkedCaseId = caseId.trim();
        this.updatedAt = now;
        record(new ParcelUpdated(id, now));
    }

    public void retire(Instant now) {
        assertActiveOrEncumbered();
        Objects.requireNonNull(now, "now");
        this.status = ParcelStatus.RETIRED;
        this.updatedAt = now;
        record(new ParcelRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == ParcelStatus.CANCELLED) {
            return;
        }
        if (status == ParcelStatus.RETIRED) {
            throw new DomainException(
                    "PARCEL_RETIRED",
                    "Parcel Retired",
                    "Retired parcels cannot be cancelled",
                    409);
        }
        Objects.requireNonNull(now, "now");
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("cancellation reason length must be between 5 and 500");
        }
        this.status = ParcelStatus.CANCELLED;
        this.updatedAt = now;
        record(new ParcelCancelled(id, reason.trim(), now));
    }

    private void assertMutable() {
        if (status == ParcelStatus.RETIRED || status == ParcelStatus.CANCELLED) {
            throw new DomainException(
                    "PARCEL_TERMINAL",
                    "Parcel Terminal",
                    "Parcel is terminal and cannot be modified",
                    409);
        }
    }

    private void assertActiveOrEncumbered() {
        if (status != ParcelStatus.ACTIVE && status != ParcelStatus.ENCUMBERED) {
            throw new DomainException(
                    "INVALID_PARCEL_STATE",
                    "Invalid Parcel State",
                    "Parcel must be ACTIVE or ENCUMBERED; was " + status,
                    409);
        }
    }

    private void assertActiveOnly() {
        if (status != ParcelStatus.ACTIVE) {
            throw new DomainException(
                    "INVALID_PARCEL_STATE",
                    "Invalid Parcel State",
                    "Parcel must be ACTIVE; was " + status,
                    409);
        }
    }

    private static void validateLease(TenureType tenureType, String leaseReference) {
        if (tenureType == TenureType.LEASEHOLD) {
            if (leaseReference == null || leaseReference.isBlank()) {
                throw new IllegalArgumentException("leaseReference is required for LEASEHOLD tenure");
            }
        }
    }

    private static void validateCaseId(String caseId) {
        if (caseId == null || caseId.isBlank()) {
            return;
        }
        String n = caseId.trim();
        if (!n.startsWith("cas_") || n.length() < 40) {
            throw new IllegalArgumentException("linkedCaseId must be a valid cas_ identifier");
        }
    }

    private static void requireDocId(String documentId) {
        requireNonBlank(documentId, "documentId");
        String n = documentId.trim();
        if (!n.startsWith("doc_") || n.length() < 40) {
            throw new IllegalArgumentException("documentId must be a valid doc_ identifier");
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

    private void record(ParcelDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ParcelDomainEvent> pullDomainEvents() {
        List<ParcelDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ParcelId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public ParcelNumber parcelNumber() { return parcelNumber; }
    public String region() { return region; }
    public Optional<String> zone() { return Optional.ofNullable(zone); }
    public String woreda() { return woreda; }
    public Optional<String> kebele() { return Optional.ofNullable(kebele); }
    public Optional<String> locality() { return Optional.ofNullable(locality); }
    public LandUse landUse() { return landUse; }
    public TenureType tenureType() { return tenureType; }
    public ParcelStatus status() { return status; }
    public AreaSquareMeters areaSquareMeters() { return areaSquareMeters; }
    public ParcelOwner owner() { return owner; }
    public Optional<String> titleDeedNumber() { return Optional.ofNullable(titleDeedNumber); }
    public Optional<String> leaseReference() { return Optional.ofNullable(leaseReference); }
    public Optional<Instant> leaseExpiresAt() { return Optional.ofNullable(leaseExpiresAt); }
    public Optional<String> linkedCaseId() { return Optional.ofNullable(linkedCaseId); }
    public Set<String> documentIds() { return Collections.unmodifiableSet(documentIds); }
    public Optional<String> encumbranceReason() { return Optional.ofNullable(encumbranceReason); }
    public Instant registeredAt() { return registeredAt; }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
