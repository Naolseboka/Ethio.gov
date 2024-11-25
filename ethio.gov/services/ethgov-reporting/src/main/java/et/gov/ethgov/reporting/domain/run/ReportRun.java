package et.gov.ethgov.reporting.domain.run;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ReportRun {
    private final ReportRunId id;
    private final TenantId tenantId;
    private final RunNumber runNumber;
    private final ReportDefinitionId reportDefinitionId;
    private RunParameters parameters;
    private Instant periodStart;
    private Instant periodEnd;
    private String requestedBy;
    private ReportRunStatus status;
    private String artifactReference;
    private String failureReason;
    private Instant requestedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant updatedAt;
    private long version;
    private final List<ReportRunDomainEvent> domainEvents = new ArrayList<>();

    private ReportRun(
            ReportRunId id,
            TenantId tenantId,
            RunNumber runNumber,
            ReportDefinitionId reportDefinitionId,
            RunParameters parameters,
            Instant periodStart,
            Instant periodEnd,
            String requestedBy,
            ReportRunStatus status,
            String artifactReference,
            String failureReason,
            Instant requestedAt,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.runNumber = runNumber;
        this.reportDefinitionId = reportDefinitionId;
        this.parameters = parameters;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.requestedBy = requestedBy;
        this.status = status;
        this.artifactReference = artifactReference;
        this.failureReason = failureReason;
        this.requestedAt = requestedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ReportRun request(
            TenantId tenantId,
            RunNumber runNumber,
            ReportDefinitionId reportDefinitionId,
            RunParameters parameters,
            Instant periodStart,
            Instant periodEnd,
            String requestedBy,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(runNumber, "runNumber");
        Objects.requireNonNull(reportDefinitionId, "reportDefinitionId");
        Objects.requireNonNull(parameters, "parameters");
        Objects.requireNonNull(now, "now");
        validatePeriod(periodStart, periodEnd);
        validateOptionalUser(requestedBy);
        ReportRun run = new ReportRun(
                ReportRunId.generate(),
                tenantId,
                runNumber,
                reportDefinitionId,
                parameters,
                periodStart,
                periodEnd,
                blankToNull(requestedBy),
                ReportRunStatus.QUEUED,
                null,
                null,
                now,
                null,
                null,
                now,
                0L);
        run.record(new ReportRunRequested(run.id, tenantId, runNumber, reportDefinitionId, now));
        return run;
    }

    public static ReportRun rehydrate(
            ReportRunId id,
            TenantId tenantId,
            RunNumber runNumber,
            ReportDefinitionId reportDefinitionId,
            RunParameters parameters,
            Instant periodStart,
            Instant periodEnd,
            String requestedBy,
            ReportRunStatus status,
            String artifactReference,
            String failureReason,
            Instant requestedAt,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt,
            long version) {
        return new ReportRun(
                id,
                tenantId,
                runNumber,
                reportDefinitionId,
                parameters,
                periodStart,
                periodEnd,
                requestedBy,
                status,
                artifactReference,
                failureReason,
                requestedAt,
                startedAt,
                completedAt,
                updatedAt,
                version);
    }

    public void start(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ReportRunStatus.QUEUED) {
            throw invalidState("start");
        }
        this.status = ReportRunStatus.RUNNING;
        this.startedAt = now;
        this.updatedAt = now;
        record(new ReportRunStarted(id, now));
    }

    public void succeed(String artifactReference, Instant now) {
        Objects.requireNonNull(now, "now");
        requireNonBlank(artifactReference, "artifactReference");
        if (artifactReference.trim().length() > 512) {
            throw new IllegalArgumentException("artifactReference length must be <= 512");
        }
        if (status != ReportRunStatus.RUNNING) {
            throw invalidState("succeed");
        }
        this.status = ReportRunStatus.SUCCEEDED;
        this.artifactReference = artifactReference.trim();
        this.completedAt = now;
        this.updatedAt = now;
        record(new ReportRunSucceeded(id, this.artifactReference, now));
    }

    public void fail(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ReportRunStatus.QUEUED && status != ReportRunStatus.RUNNING) {
            throw invalidState("fail");
        }
        this.status = ReportRunStatus.FAILED;
        this.failureReason = reason.trim();
        this.completedAt = now;
        this.updatedAt = now;
        record(new ReportRunFailed(id, this.failureReason, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == ReportRunStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ReportRunStatus.QUEUED) {
            throw invalidState("cancel");
        }
        this.status = ReportRunStatus.CANCELLED;
        this.failureReason = reason.trim();
        this.completedAt = now;
        this.updatedAt = now;
        record(new ReportRunCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_RUN_STATE",
                "Invalid Run State",
                "Cannot " + action + " report run in status " + status,
                409);
    }

    private static void validatePeriod(Instant periodStart, Instant periodEnd) {
        if (periodStart != null && periodEnd != null && periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("periodEnd must be on or after periodStart");
        }
    }

    private static void validateOptionalUser(String requestedBy) {
        if (requestedBy == null || requestedBy.isBlank()) {
            return;
        }
        String n = requestedBy.trim();
        if (!n.startsWith("usr_") || n.length() < 40) {
            throw new IllegalArgumentException("requestedBy must be a valid usr_ identifier");
        }
    }

    private static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
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

    private void record(ReportRunDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ReportRunDomainEvent> pullDomainEvents() {
        List<ReportRunDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ReportRunId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public RunNumber runNumber() {
        return runNumber;
    }

    public ReportDefinitionId reportDefinitionId() {
        return reportDefinitionId;
    }

    public RunParameters parameters() {
        return parameters;
    }

    public Optional<Instant> periodStart() {
        return Optional.ofNullable(periodStart);
    }

    public Optional<Instant> periodEnd() {
        return Optional.ofNullable(periodEnd);
    }

    public Optional<String> requestedBy() {
        return Optional.ofNullable(requestedBy);
    }

    public ReportRunStatus status() {
        return status;
    }

    public Optional<String> artifactReference() {
        return Optional.ofNullable(artifactReference);
    }

    public Optional<String> failureReason() {
        return Optional.ofNullable(failureReason);
    }

    public Instant requestedAt() {
        return requestedAt;
    }

    public Optional<Instant> startedAt() {
        return Optional.ofNullable(startedAt);
    }

    public Optional<Instant> completedAt() {
        return Optional.ofNullable(completedAt);
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
