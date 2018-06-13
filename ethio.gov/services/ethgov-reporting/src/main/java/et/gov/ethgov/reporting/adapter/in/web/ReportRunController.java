package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.run.CancelReportRunUseCase;
import et.gov.ethgov.reporting.application.run.FailReportRunUseCase;
import et.gov.ethgov.reporting.application.run.GetReportRunUseCase;
import et.gov.ethgov.reporting.application.run.RequestReportRunUseCase;
import et.gov.ethgov.reporting.application.run.StartReportRunUseCase;
import et.gov.ethgov.reporting.application.run.SucceedReportRunUseCase;
import et.gov.ethgov.reporting.domain.run.ReportRun;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/report-runs")
public class ReportRunController {

    private final RequestReportRunUseCase requestRun;
    private final StartReportRunUseCase startRun;
    private final SucceedReportRunUseCase succeedRun;
    private final FailReportRunUseCase failRun;
    private final CancelReportRunUseCase cancelRun;
    private final GetReportRunUseCase getRun;

    public ReportRunController(
            RequestReportRunUseCase requestRun,
            StartReportRunUseCase startRun,
            SucceedReportRunUseCase succeedRun,
            FailReportRunUseCase failRun,
            CancelReportRunUseCase cancelRun,
            GetReportRunUseCase getRun) {
        this.requestRun = requestRun;
        this.startRun = startRun;
        this.succeedRun = succeedRun;
        this.failRun = failRun;
        this.cancelRun = cancelRun;
        this.getRun = getRun;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RunIdResponse request(@Valid @RequestBody RequestRunRequest request) {
        return new RunIdResponse(requestRun
                .execute(new RequestReportRunUseCase.Command(
                        request.tenantId(),
                        request.reportDefinitionId(),
                        request.parameters(),
                        request.periodStart(),
                        request.periodEnd(),
                        request.requestedBy()))
                .value());
    }

    @GetMapping("/lookup")
    public RunResponse lookup(@RequestParam String tenantId, @RequestParam String runNumber) {
        return RunResponse.from(getRun.byNumber(tenantId, runNumber));
    }

    @GetMapping("/{reportRunId}")
    public RunResponse get(@PathVariable String reportRunId) {
        return RunResponse.from(getRun.byId(reportRunId));
    }

    @PostMapping("/{reportRunId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(@PathVariable String reportRunId) {
        startRun.execute(reportRunId);
    }

    @PostMapping("/{reportRunId}/succeed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void succeed(
            @PathVariable String reportRunId, @Valid @RequestBody SucceedRequest request) {
        succeedRun.execute(reportRunId, request.artifactReference());
    }

    @PostMapping("/{reportRunId}/fail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fail(@PathVariable String reportRunId, @Valid @RequestBody ReasonRequest request) {
        failRun.execute(reportRunId, request.reason());
    }

    @PostMapping("/{reportRunId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String reportRunId, @Valid @RequestBody ReasonRequest request) {
        cancelRun.execute(reportRunId, request.reason());
    }

    public record RequestRunRequest(
            @NotBlank String tenantId,
            @NotBlank String reportDefinitionId,
            String parameters,
            String periodStart,
            String periodEnd,
            String requestedBy) {}

    public record SucceedRequest(@NotBlank @Size(max = 512) String artifactReference) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record RunIdResponse(String reportRunId) {}

    public record RunResponse(
            String reportRunId,
            String tenantId,
            String runNumber,
            String reportDefinitionId,
            String parameters,
            String periodStart,
            String periodEnd,
            String requestedBy,
            String status,
            String artifactReference,
            String failureReason) {

        static RunResponse from(ReportRun run) {
            return new RunResponse(
                    run.id().value(),
                    run.tenantId().value(),
                    run.runNumber().value(),
                    run.reportDefinitionId().value(),
                    run.parameters().json(),
                    run.periodStart().map(Object::toString).orElse(null),
                    run.periodEnd().map(Object::toString).orElse(null),
                    run.requestedBy().orElse(null),
                    run.status().name(),
                    run.artifactReference().orElse(null),
                    run.failureReason().orElse(null));
        }
    }
}
