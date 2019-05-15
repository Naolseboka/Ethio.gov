package et.gov.ethgov.casemgmt.domain.casefile;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Governed government case file — the file that ties parties, workflow, and documents together.
 */
public final class GovernmentCase {

    private static final int MAX_DOCUMENTS = 50;
    private static final int MAX_NOTES = 200;

    private final CaseId id;
    private final TenantId tenantId;
    private final CaseNumber caseNumber;
    private final CaseType type;
    private String title;
    private String description;
    private CaseStatus status;
    private CasePriority priority;
    private CaseParty party;
    private String assigneeAccountId;
    private String owningUnitId;
    private String processInstanceId;
    private final Set<String> documentIds = new LinkedHashSet<>();
    private final List<CaseNote> notes = new ArrayList<>();
    private final Instant openedAt;
    private Instant closedAt;
    private Instant updatedAt;
    private long entityVersion;
    private final List<CaseDomainEvent> domainEvents = new ArrayList<>();

    private GovernmentCase(
            CaseId id,
            TenantId tenantId,
            CaseNumber caseNumber,
            CaseType type,
            String title,
            String description,
            CaseStatus status,
            CasePriority priority,
            CaseParty party,
            String assigneeAccountId,
            String owningUnitId,
            String processInstanceId,
            Set<String> documentIds,
            List<CaseNote> notes,
            Instant openedAt,
            Instant closedAt,
            Instant updatedAt,
            long entityVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.caseNumber = caseNumber;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.party = party;
        this.assigneeAccountId = assigneeAccountId;
        this.owningUnitId = owningUnitId;
        this.processInstanceId = processInstanceId;
        this.documentIds.addAll(documentIds);
        this.notes.addAll(notes);
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.updatedAt = updatedAt;
        this.entityVersion = entityVersion;
    }

    public static GovernmentCase open(
            TenantId tenantId,
            CaseNumber caseNumber,
            CaseType type,
            String title,
            String description,
            CasePriority priority,
            CaseParty party,
            String owningUnitId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(caseNumber, "caseNumber");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(party, "party");
        GovernmentCase governmentCase = new GovernmentCase(
                CaseId.generate(),
                tenantId,
                caseNumber,
                type,
                requireTitle(title),
                normalizeOptional(description, 2000),
                CaseStatus.OPEN,
                priority,
                party,
                null,
                normalizeOu(owningUnitId),
                null,
                Set.of(),
                List.of(),
                now,
                null,
                now,
                0);
        governmentCase.record(new CaseOpened(
                governmentCase.id, tenantId, caseNumber, type, now));
        return governmentCase;
    }

    public static GovernmentCase rehydrate(
            CaseId id,
            TenantId tenantId,
            CaseNumber caseNumber,
            CaseType type,
            String title,
            String description,
            CaseStatus status,
            CasePriority priority,
            CaseParty party,
            String assigneeAccountId,
            String owningUnitId,
            String processInstanceId,
            Set<String> documentIds,
            List<CaseNote> notes,
            Instant openedAt,
            Instant closedAt,
            Instant updatedAt,
            long entityVersion) {
        return new GovernmentCase(
                id,
                tenantId,
                caseNumber,
                type,
                title,
                description,
                status,
                priority,
                party,
                assigneeAccountId,
                owningUnitId,
                processInstanceId,
                documentIds,
                notes,
                openedAt,
                closedAt,
                updatedAt,
                entityVersion);
    }

    public void updateDetails(String title, String description, CasePriority priority, Instant now) {
        ensureMutable();
        Objects.requireNonNull(priority, "priority");
        this.title = requireTitle(title);
        this.description = normalizeOptional(description, 2000);
        this.priority = priority;
        touch(now);
        record(new CaseUpdated(id, now));
    }

    public void assign(String assigneeAccountId, Instant now) {
        ensureMutable();
        String assignee = requireUser(assigneeAccountId);
        if (assignee.equals(this.assigneeAccountId)) {
            return;
        }
        this.assigneeAccountId = assignee;
        if (status == CaseStatus.OPEN) {
            transitionTo(CaseStatus.IN_PROGRESS, now);
        }
        touch(now);
        record(new CaseAssigned(id, assignee, now));
    }

    public void changeStatus(CaseStatus target, Instant now) {
        ensureNotTerminalForStatusChange();
        Objects.requireNonNull(target, "target");
        if (target == status) {
            return;
        }
        if (!allowedTransitions(status).contains(target)) {
            throw new DomainException(
                    "INVALID_STATUS_TRANSITION",
                    "Invalid Status Transition",
                    "Cannot transition case from " + status + " to " + target,
                    409);
        }
        transitionTo(target, now);
        if (target == CaseStatus.CLOSED) {
            this.closedAt = now;
            record(new CaseClosed(id, now));
        }
        touch(now);
    }

    public void linkWorkflow(String processInstanceId, Instant now) {
        ensureMutable();
        Objects.requireNonNull(processInstanceId, "processInstanceId");
        String wpi = processInstanceId.trim();
        if (!wpi.startsWith("wpi_") || wpi.length() < 40) {
            throw new IllegalArgumentException("processInstanceId must be a ProcessInstanceId");
        }
        if (this.processInstanceId != null && !this.processInstanceId.equals(wpi)) {
            throw new DomainException(
                    "WORKFLOW_ALREADY_LINKED",
                    "Workflow Already Linked",
                    "Case already linked to a process instance",
                    409);
        }
        if (wpi.equals(this.processInstanceId)) {
            return;
        }
        this.processInstanceId = wpi;
        touch(now);
        record(new CaseWorkflowLinked(id, wpi, now));
    }

    public void attachDocument(String documentId, Instant now) {
        ensureMutable();
        Objects.requireNonNull(documentId, "documentId");
        String doc = documentId.trim();
        if (!doc.startsWith("doc_") || doc.length() < 40) {
            throw new IllegalArgumentException("documentId must be a DocumentId");
        }
        if (documentIds.contains(doc)) {
            return;
        }
        if (documentIds.size() >= MAX_DOCUMENTS) {
            throw new DomainException(
                    "TOO_MANY_DOCUMENTS",
                    "Too Many Documents",
                    "Case cannot attach more than " + MAX_DOCUMENTS + " documents",
                    409);
        }
        documentIds.add(doc);
        touch(now);
        record(new CaseDocumentAttached(id, doc, now));
    }

    public void addNote(String authorAccountId, String body, Instant now) {
        ensureMutable();
        if (notes.size() >= MAX_NOTES) {
            throw new DomainException(
                    "TOO_MANY_NOTES",
                    "Too Many Notes",
                    "Case cannot have more than " + MAX_NOTES + " notes",
                    409);
        }
        CaseNote note = CaseNote.of(authorAccountId, body, now);
        notes.add(note);
        touch(now);
        record(new CaseNoteAdded(id, note.noteId(), now));
    }

    public void cancel(String reason, Instant now) {
        if (status == CaseStatus.CANCELLED) {
            return;
        }
        if (status == CaseStatus.CLOSED) {
            throw new DomainException(
                    "CASE_CLOSED",
                    "Case Closed",
                    "Closed cases cannot be cancelled",
                    409);
        }
        Objects.requireNonNull(reason, "reason");
        String normalized = reason.trim();
        if (normalized.length() < 5 || normalized.length() > 500) {
            throw new IllegalArgumentException("cancellation reason length must be between 5 and 500");
        }
        CaseStatus from = status;
        this.status = CaseStatus.CANCELLED;
        this.closedAt = now;
        touch(now);
        record(new CaseStatusChanged(id, from, CaseStatus.CANCELLED, now));
        record(new CaseCancelled(id, normalized, now));
    }

    public List<CaseDomainEvent> pullDomainEvents() {
        List<CaseDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void transitionTo(CaseStatus target, Instant now) {
        CaseStatus from = this.status;
        this.status = target;
        record(new CaseStatusChanged(id, from, target, now));
    }

    private static EnumSet<CaseStatus> allowedTransitions(CaseStatus from) {
        return switch (from) {
            case DRAFT -> EnumSet.of(CaseStatus.OPEN, CaseStatus.CANCELLED);
            case OPEN -> EnumSet.of(
                    CaseStatus.IN_PROGRESS, CaseStatus.PENDING, CaseStatus.CLOSED, CaseStatus.CANCELLED);
            case IN_PROGRESS -> EnumSet.of(
                    CaseStatus.PENDING, CaseStatus.RESOLVED, CaseStatus.CLOSED, CaseStatus.CANCELLED);
            case PENDING -> EnumSet.of(
                    CaseStatus.IN_PROGRESS, CaseStatus.RESOLVED, CaseStatus.CANCELLED);
            case RESOLVED -> EnumSet.of(CaseStatus.CLOSED, CaseStatus.IN_PROGRESS);
            case CLOSED, CANCELLED -> EnumSet.noneOf(CaseStatus.class);
        };
    }

    private void ensureMutable() {
        if (status == CaseStatus.CLOSED || status == CaseStatus.CANCELLED) {
            throw new DomainException(
                    "CASE_TERMINAL",
                    "Case Terminal",
                    "Closed or cancelled cases cannot be modified",
                    409);
        }
    }

    private void ensureNotTerminalForStatusChange() {
        if (status == CaseStatus.CANCELLED) {
            throw new DomainException(
                    "CASE_CANCELLED",
                    "Case Cancelled",
                    "Cancelled cases cannot change status",
                    409);
        }
        if (status == CaseStatus.CLOSED) {
            throw new DomainException(
                    "CASE_CLOSED",
                    "Case Closed",
                    "Closed cases cannot change status",
                    409);
        }
    }

    private static String requireTitle(String title) {
        Objects.requireNonNull(title, "title");
        String normalized = title.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("title length must be between 2 and 200");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > max) {
            throw new IllegalArgumentException("description must be <= " + max + " characters");
        }
        return normalized;
    }

    private static String requireUser(String accountId) {
        Objects.requireNonNull(accountId, "assigneeAccountId");
        String normalized = accountId.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("assigneeAccountId must be a UserAccountId");
        }
        return normalized;
    }

    private static String normalizeOu(String owningUnitId) {
        if (owningUnitId == null || owningUnitId.isBlank()) {
            return null;
        }
        String normalized = owningUnitId.trim();
        if (!normalized.startsWith("ou_") || normalized.length() < 39) {
            throw new IllegalArgumentException("owningUnitId must be an OrganizationalUnitId");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(CaseDomainEvent event) {
        domainEvents.add(event);
    }

    public CaseId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public CaseNumber caseNumber() { return caseNumber; }
    public CaseType type() { return type; }
    public String title() { return title; }
    public Optional<String> description() { return Optional.ofNullable(description); }
    public CaseStatus status() { return status; }
    public CasePriority priority() { return priority; }
    public CaseParty party() { return party; }
    public Optional<String> assigneeAccountId() { return Optional.ofNullable(assigneeAccountId); }
    public Optional<String> owningUnitId() { return Optional.ofNullable(owningUnitId); }
    public Optional<String> processInstanceId() { return Optional.ofNullable(processInstanceId); }
    public Set<String> documentIds() { return Set.copyOf(documentIds); }
    public List<CaseNote> notes() { return List.copyOf(notes); }
    public Instant openedAt() { return openedAt; }
    public Optional<Instant> closedAt() { return Optional.ofNullable(closedAt); }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return entityVersion; }
}
