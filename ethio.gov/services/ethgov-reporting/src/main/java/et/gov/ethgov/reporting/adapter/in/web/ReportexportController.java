package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.reportexport.ActivateReportexportUseCase;
import et.gov.ethgov.reporting.application.reportexport.CreateReportexportUseCase;
import et.gov.ethgov.reporting.application.reportexport.GetReportexportUseCase;
import et.gov.ethgov.reporting.application.reportexport.ListReportexportUseCase;
import et.gov.ethgov.reporting.application.reportexport.SuspendReportexportUseCase;
import et.gov.ethgov.reporting.application.reportexport.UpdateReportexportUseCase;
import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
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
@RequestMapping("/v1/reporting/reportexport")
public class ReportexportController {
    private final CreateReportexportUseCase createUseCase;
    private final UpdateReportexportUseCase updateUseCase;
    private final ActivateReportexportUseCase activateUseCase;
    private final SuspendReportexportUseCase suspendUseCase;
    private final GetReportexportUseCase getUseCase;
    private final ListReportexportUseCase listUseCase;

    public ReportexportController(
            CreateReportexportUseCase createUseCase,
            UpdateReportexportUseCase updateUseCase,
            ActivateReportexportUseCase activateUseCase,
            SuspendReportexportUseCase suspendUseCase,
            GetReportexportUseCase getUseCase,
            ListReportexportUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportexportIdResponse create(@Valid @RequestBody CreateReportexportRequest request) {
        var id = createUseCase.execute(new CreateReportexportUseCase.CreateReportexportCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReportexportIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReportexportResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReportexportResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReportexportIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReportexportRequest request) {
        var updated = updateUseCase.execute(new UpdateReportexportUseCase.UpdateReportexportCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReportexportIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReportexportUseCase.ActivateReportexportCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReportexportRequest request) {
        suspendUseCase.execute(new SuspendReportexportUseCase.SuspendReportexportCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReportexportResponse toResponse(Reportexport aggregate) {
        return new ReportexportResponse(
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

    public record CreateReportexportRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReportexportRequest(String title, String description) {}

    public record SuspendReportexportRequest(@NotBlank String reason) {}

    public record ReportexportIdResponse(String id) {}

    public record ReportexportResponse(
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
