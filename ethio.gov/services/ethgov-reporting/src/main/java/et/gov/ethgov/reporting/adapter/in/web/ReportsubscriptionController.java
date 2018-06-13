package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.reportsubscription.ActivateReportsubscriptionUseCase;
import et.gov.ethgov.reporting.application.reportsubscription.CreateReportsubscriptionUseCase;
import et.gov.ethgov.reporting.application.reportsubscription.GetReportsubscriptionUseCase;
import et.gov.ethgov.reporting.application.reportsubscription.ListReportsubscriptionUseCase;
import et.gov.ethgov.reporting.application.reportsubscription.SuspendReportsubscriptionUseCase;
import et.gov.ethgov.reporting.application.reportsubscription.UpdateReportsubscriptionUseCase;
import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
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
@RequestMapping("/v1/reporting/reportsubscription")
public class ReportsubscriptionController {
    private final CreateReportsubscriptionUseCase createUseCase;
    private final UpdateReportsubscriptionUseCase updateUseCase;
    private final ActivateReportsubscriptionUseCase activateUseCase;
    private final SuspendReportsubscriptionUseCase suspendUseCase;
    private final GetReportsubscriptionUseCase getUseCase;
    private final ListReportsubscriptionUseCase listUseCase;

    public ReportsubscriptionController(
            CreateReportsubscriptionUseCase createUseCase,
            UpdateReportsubscriptionUseCase updateUseCase,
            ActivateReportsubscriptionUseCase activateUseCase,
            SuspendReportsubscriptionUseCase suspendUseCase,
            GetReportsubscriptionUseCase getUseCase,
            ListReportsubscriptionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportsubscriptionIdResponse create(@Valid @RequestBody CreateReportsubscriptionRequest request) {
        var id = createUseCase.execute(new CreateReportsubscriptionUseCase.CreateReportsubscriptionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReportsubscriptionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReportsubscriptionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReportsubscriptionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReportsubscriptionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReportsubscriptionRequest request) {
        var updated = updateUseCase.execute(new UpdateReportsubscriptionUseCase.UpdateReportsubscriptionCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReportsubscriptionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReportsubscriptionUseCase.ActivateReportsubscriptionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReportsubscriptionRequest request) {
        suspendUseCase.execute(new SuspendReportsubscriptionUseCase.SuspendReportsubscriptionCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReportsubscriptionResponse toResponse(Reportsubscription aggregate) {
        return new ReportsubscriptionResponse(
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

    public record CreateReportsubscriptionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReportsubscriptionRequest(String title, String description) {}

    public record SuspendReportsubscriptionRequest(@NotBlank String reason) {}

    public record ReportsubscriptionIdResponse(String id) {}

    public record ReportsubscriptionResponse(
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
