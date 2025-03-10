package et.gov.ethgov.revenue.domain.bill;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class RevenueBill {
    private final BillId id;
    private final TenantId tenantId;
    private final BillNumber billNumber;
    private RevenueType type;
    private String description;
    private BillStatus status;
    private BillParty party;
    private Money amount;
    private Instant periodStart;
    private Instant periodEnd;
    private Instant dueAt;
    private Instant issuedAt;
    private Instant paidAt;
    private String paymentReference;
    private String licenseId;
    private String permitId;
    private String parcelId;
    private String linkedCaseId;
    private String cancelReason;
    private String writeOffReason;
    private Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<BillDomainEvent> domainEvents = new ArrayList<>();

    private RevenueBill(
            BillId id,
            TenantId tenantId,
            BillNumber billNumber,
            RevenueType type,
            String description,
            BillStatus status,
            BillParty party,
            Money amount,
            Instant periodStart,
            Instant periodEnd,
            Instant dueAt,
            Instant issuedAt,
            Instant paidAt,
            String paymentReference,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId,
            String cancelReason,
            String writeOffReason,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.billNumber = billNumber;
        this.type = type;
        this.description = description;
        this.status = status;
        this.party = party;
        this.amount = amount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.dueAt = dueAt;
        this.issuedAt = issuedAt;
        this.paidAt = paidAt;
        this.paymentReference = paymentReference;
        this.licenseId = licenseId;
        this.permitId = permitId;
        this.parcelId = parcelId;
        this.linkedCaseId = linkedCaseId;
        this.cancelReason = cancelReason;
        this.writeOffReason = writeOffReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static RevenueBill create(
            TenantId tenantId,
            BillNumber billNumber,
            RevenueType type,
            String description,
            BillParty party,
            Money amount,
            Instant periodStart,
            Instant periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(billNumber, "billNumber");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(party, "party");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(now, "now");
        requireNonBlank(description, "description");
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("amount must be positive");
        }
        validatePeriod(periodStart, periodEnd);
        validateOptionalId(licenseId, "lic_", "licenseId");
        validateOptionalId(permitId, "prm_", "permitId");
        validateOptionalId(parcelId, "par_", "parcelId");
        validateOptionalId(linkedCaseId, "cas_", "linkedCaseId");
        RevenueBill bill = new RevenueBill(
                BillId.generate(),
                tenantId,
                billNumber,
                type,
                description.trim(),
                BillStatus.DRAFT,
                party,
                amount,
                periodStart,
                periodEnd,
                null,
                null,
                null,
                null,
                blankToNull(licenseId),
                blankToNull(permitId),
                blankToNull(parcelId),
                blankToNull(linkedCaseId),
                null,
                null,
                now,
                now,
                0L);
        bill.record(new BillCreated(bill.id, tenantId, billNumber, type, now));
        return bill;
    }

    public static RevenueBill rehydrate(
            BillId id,
            TenantId tenantId,
            BillNumber billNumber,
            RevenueType type,
            String description,
            BillStatus status,
            BillParty party,
            Money amount,
            Instant periodStart,
            Instant periodEnd,
            Instant dueAt,
            Instant issuedAt,
            Instant paidAt,
            String paymentReference,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId,
            String cancelReason,
            String writeOffReason,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        return new RevenueBill(
                id, tenantId, billNumber, type, description, status, party, amount,
                periodStart, periodEnd, dueAt, issuedAt, paidAt, paymentReference,
                licenseId, permitId, parcelId, linkedCaseId, cancelReason, writeOffReason,
                createdAt, updatedAt, version);
    }

    public void update(
            String description,
            Money amount,
            Instant periodStart,
            Instant periodEnd,
            String licenseId,
            String permitId,
            String parcelId,
            String linkedCaseId,
            Instant now) {
        if (status != BillStatus.DRAFT) {
            throw new DomainException(
                    "BILL_NOT_EDITABLE",
                    "Bill Not Editable",
                    "Only DRAFT bills can be updated",
                    409);
        }
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(now, "now");
        requireNonBlank(description, "description");
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("amount must be positive");
        }
        validatePeriod(periodStart, periodEnd);
        validateOptionalId(licenseId, "lic_", "licenseId");
        validateOptionalId(permitId, "prm_", "permitId");
        validateOptionalId(parcelId, "par_", "parcelId");
        validateOptionalId(linkedCaseId, "cas_", "linkedCaseId");
        this.description = description.trim();
        this.amount = amount;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.licenseId = blankToNull(licenseId);
        this.permitId = blankToNull(permitId);
        this.parcelId = blankToNull(parcelId);
        this.linkedCaseId = blankToNull(linkedCaseId);
        this.updatedAt = now;
        record(new BillUpdated(id, now));
    }

    public void issue(Instant dueAt, Instant now) {
        Objects.requireNonNull(dueAt, "dueAt");
        Objects.requireNonNull(now, "now");
        if (status != BillStatus.DRAFT) {
            throw invalidState("issue");
        }
        if (!dueAt.isAfter(now)) {
            throw new IllegalArgumentException("dueAt must be in the future relative to issue time");
        }
        this.dueAt = dueAt;
        this.issuedAt = now;
        this.status = BillStatus.ISSUED;
        this.updatedAt = now;
        record(new BillIssued(id, dueAt, now));
    }

    public void markOverdue(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != BillStatus.ISSUED) {
            throw invalidState("mark overdue");
        }
        if (dueAt == null || now.isBefore(dueAt)) {
            throw new DomainException(
                    "NOT_YET_DUE",
                    "Not Yet Due",
                    "Bill cannot be marked overdue before dueAt",
                    409);
        }
        this.status = BillStatus.OVERDUE;
        this.updatedAt = now;
        record(new BillMarkedOverdue(id, now));
    }

    public void markPaid(String paymentReference, Instant now) {
        Objects.requireNonNull(now, "now");
        requireNonBlank(paymentReference, "paymentReference");
        String ref = paymentReference.trim();
        if (ref.length() < 5 || ref.length() > 120) {
            throw new IllegalArgumentException("paymentReference length must be between 5 and 120");
        }
        if (status != BillStatus.ISSUED && status != BillStatus.OVERDUE) {
            throw invalidState("mark paid");
        }
        this.status = BillStatus.PAID;
        this.paymentReference = ref;
        this.paidAt = now;
        this.updatedAt = now;
        record(new BillPaid(id, ref, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == BillStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status == BillStatus.PAID || status == BillStatus.WRITTEN_OFF) {
            throw invalidState("cancel");
        }
        this.status = BillStatus.CANCELLED;
        this.cancelReason = reason.trim();
        this.updatedAt = now;
        record(new BillCancelled(id, this.cancelReason, now));
    }

    public void writeOff(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != BillStatus.ISSUED && status != BillStatus.OVERDUE) {
            throw invalidState("write off");
        }
        this.status = BillStatus.WRITTEN_OFF;
        this.writeOffReason = reason.trim();
        this.updatedAt = now;
        record(new BillWrittenOff(id, this.writeOffReason, now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_BILL_STATE",
                "Invalid Bill State",
                "Cannot " + action + " bill in status " + status,
                409);
    }

    private static void validatePeriod(Instant periodStart, Instant periodEnd) {
        if (periodStart != null && periodEnd != null && !periodEnd.isAfter(periodStart)) {
            throw new IllegalArgumentException("periodEnd must be after periodStart");
        }
    }

    private static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
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

    private void record(BillDomainEvent event) {
        domainEvents.add(event);
    }

    public List<BillDomainEvent> pullDomainEvents() {
        List<BillDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public BillId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public BillNumber billNumber() { return billNumber; }
    public RevenueType type() { return type; }
    public String description() { return description; }
    public BillStatus status() { return status; }
    public BillParty party() { return party; }
    public Money amount() { return amount; }
    public Optional<Instant> periodStart() { return Optional.ofNullable(periodStart); }
    public Optional<Instant> periodEnd() { return Optional.ofNullable(periodEnd); }
    public Optional<Instant> dueAt() { return Optional.ofNullable(dueAt); }
    public Optional<Instant> issuedAt() { return Optional.ofNullable(issuedAt); }
    public Optional<Instant> paidAt() { return Optional.ofNullable(paidAt); }
    public Optional<String> paymentReference() { return Optional.ofNullable(paymentReference); }
    public Optional<String> licenseId() { return Optional.ofNullable(licenseId); }
    public Optional<String> permitId() { return Optional.ofNullable(permitId); }
    public Optional<String> parcelId() { return Optional.ofNullable(parcelId); }
    public Optional<String> linkedCaseId() { return Optional.ofNullable(linkedCaseId); }
    public Optional<String> cancelReason() { return Optional.ofNullable(cancelReason); }
    public Optional<String> writeOffReason() { return Optional.ofNullable(writeOffReason); }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
