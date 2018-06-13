package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.reportfilter.ActivateReportfilterUseCase;
import et.gov.ethgov.reporting.application.reportfilter.CreateReportfilterUseCase;
import et.gov.ethgov.reporting.application.reportfilter.GetReportfilterUseCase;
import et.gov.ethgov.reporting.application.reportfilter.ListReportfilterUseCase;
import et.gov.ethgov.reporting.application.reportfilter.SuspendReportfilterUseCase;
import et.gov.ethgov.reporting.application.reportfilter.UpdateReportfilterUseCase;
import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
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
@RequestMapping("/v1/reporting/reportfilter")
public class ReportfilterController {
    private final CreateReportfilterUseCase createUseCase;
    private final UpdateReportfilterUseCase updateUseCase;
    private final ActivateReportfilterUseCase activateUseCase;
    private final SuspendReportfilterUseCase suspendUseCase;
    private final GetReportfilterUseCase getUseCase;
    private final ListReportfilterUseCase listUseCase;

    public ReportfilterController(
            CreateReportfilterUseCase createUseCase,
            UpdateReportfilterUseCase updateUseCase,
            ActivateReportfilterUseCase activateUseCase,
            SuspendReportfilterUseCase suspendUseCase,
            GetReportfilterUseCase getUseCase,
            ListReportfilterUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportfilterIdResponse create(@Valid @RequestBody CreateReportfilterRequest request) {
        var id = createUseCase.execute(new CreateReportfilterUseCase.CreateReportfilterCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReportfilterIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReportfilterResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReportfilterResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReportfilterIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReportfilterRequest request) {
        var updated = updateUseCase.execute(new UpdateReportfilterUseCase.UpdateReportfilterCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReportfilterIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReportfilterUseCase.ActivateReportfilterCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReportfilterRequest request) {
        suspendUseCase.execute(new SuspendReportfilterUseCase.SuspendReportfilterCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReportfilterResponse toResponse(Reportfilter aggregate) {
        return new ReportfilterResponse(
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

    public record CreateReportfilterRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReportfilterRequest(String title, String description) {}

    public record SuspendReportfilterRequest(@NotBlank String reason) {}

    public record ReportfilterIdResponse(String id) {}

    public record ReportfilterResponse(
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
