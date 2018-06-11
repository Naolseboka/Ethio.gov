package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.report.ActivateReportUseCase;
import et.gov.ethgov.reporting.application.report.CreateReportUseCase;
import et.gov.ethgov.reporting.application.report.GetReportUseCase;
import et.gov.ethgov.reporting.application.report.ListReportUseCase;
import et.gov.ethgov.reporting.application.report.SuspendReportUseCase;
import et.gov.ethgov.reporting.application.report.UpdateReportUseCase;
import et.gov.ethgov.reporting.domain.report.Report;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reporting/report")
public class ReportController {
    private final CreateReportUseCase createUseCase;
    private final UpdateReportUseCase updateUseCase;
    private final ActivateReportUseCase activateUseCase;
    private final SuspendReportUseCase suspendUseCase;
    private final GetReportUseCase getUseCase;
    private final ListReportUseCase listUseCase;

    public ReportController(
            CreateReportUseCase createUseCase,
            UpdateReportUseCase updateUseCase,
            ActivateReportUseCase activateUseCase,
            SuspendReportUseCase suspendUseCase,
            GetReportUseCase getUseCase,
            ListReportUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportIdResponse create(@Valid @RequestBody CreateReportRequest request) {
        var id = createUseCase.execute(new CreateReportUseCase.CreateReportCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReportIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReportResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReportIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReportRequest request) {
        var updated = updateUseCase.execute(new UpdateReportUseCase.UpdateReportCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReportIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReportUseCase.ActivateReportCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReportRequest request) {
        suspendUseCase.execute(new SuspendReportUseCase.SuspendReportCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReportResponse toResponse(Report aggregate) {
        return new ReportResponse(
                aggregate.id().toString(),
                aggregate.referenceCode(),
                aggregate.title(),
                aggregate.description(),
                aggregate.tenantId(),
                aggregate.status().name(),
                aggregate.createdAt().toString(),
                aggregate.updatedAt().toString(),
                aggregate.version());
    }

    public record CreateReportRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReportRequest(String title, String description) {}

    public record SuspendReportRequest(@NotBlank String reason) {}

    public record ReportIdResponse(String id) {}

    public record ReportResponse(
            String id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            String createdAt,
            String updatedAt,
            long version) {}
}
