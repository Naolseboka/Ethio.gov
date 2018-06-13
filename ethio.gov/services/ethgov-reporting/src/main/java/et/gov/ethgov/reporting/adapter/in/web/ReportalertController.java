package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.reportalert.ActivateReportalertUseCase;
import et.gov.ethgov.reporting.application.reportalert.CreateReportalertUseCase;
import et.gov.ethgov.reporting.application.reportalert.GetReportalertUseCase;
import et.gov.ethgov.reporting.application.reportalert.ListReportalertUseCase;
import et.gov.ethgov.reporting.application.reportalert.SuspendReportalertUseCase;
import et.gov.ethgov.reporting.application.reportalert.UpdateReportalertUseCase;
import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
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
@RequestMapping("/v1/reporting/reportalert")
public class ReportalertController {
    private final CreateReportalertUseCase createUseCase;
    private final UpdateReportalertUseCase updateUseCase;
    private final ActivateReportalertUseCase activateUseCase;
    private final SuspendReportalertUseCase suspendUseCase;
    private final GetReportalertUseCase getUseCase;
    private final ListReportalertUseCase listUseCase;

    public ReportalertController(
            CreateReportalertUseCase createUseCase,
            UpdateReportalertUseCase updateUseCase,
            ActivateReportalertUseCase activateUseCase,
            SuspendReportalertUseCase suspendUseCase,
            GetReportalertUseCase getUseCase,
            ListReportalertUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportalertIdResponse create(@Valid @RequestBody CreateReportalertRequest request) {
        var id = createUseCase.execute(new CreateReportalertUseCase.CreateReportalertCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReportalertIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReportalertResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReportalertResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReportalertIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReportalertRequest request) {
        var updated = updateUseCase.execute(new UpdateReportalertUseCase.UpdateReportalertCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReportalertIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReportalertUseCase.ActivateReportalertCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReportalertRequest request) {
        suspendUseCase.execute(new SuspendReportalertUseCase.SuspendReportalertCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReportalertResponse toResponse(Reportalert aggregate) {
        return new ReportalertResponse(
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

    public record CreateReportalertRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReportalertRequest(String title, String description) {}

    public record SuspendReportalertRequest(@NotBlank String reason) {}

    public record ReportalertIdResponse(String id) {}

    public record ReportalertResponse(
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
