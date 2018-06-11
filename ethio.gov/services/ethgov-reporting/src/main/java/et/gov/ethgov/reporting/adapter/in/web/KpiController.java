package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.kpi.ActivateKpiUseCase;
import et.gov.ethgov.reporting.application.kpi.CreateKpiUseCase;
import et.gov.ethgov.reporting.application.kpi.GetKpiUseCase;
import et.gov.ethgov.reporting.application.kpi.ListKpiUseCase;
import et.gov.ethgov.reporting.application.kpi.SuspendKpiUseCase;
import et.gov.ethgov.reporting.application.kpi.UpdateKpiUseCase;
import et.gov.ethgov.reporting.domain.kpi.Kpi;
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
@RequestMapping("/v1/reporting/kpi")
public class KpiController {
    private final CreateKpiUseCase createUseCase;
    private final UpdateKpiUseCase updateUseCase;
    private final ActivateKpiUseCase activateUseCase;
    private final SuspendKpiUseCase suspendUseCase;
    private final GetKpiUseCase getUseCase;
    private final ListKpiUseCase listUseCase;

    public KpiController(
            CreateKpiUseCase createUseCase,
            UpdateKpiUseCase updateUseCase,
            ActivateKpiUseCase activateUseCase,
            SuspendKpiUseCase suspendUseCase,
            GetKpiUseCase getUseCase,
            ListKpiUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KpiIdResponse create(@Valid @RequestBody CreateKpiRequest request) {
        var id = createUseCase.execute(new CreateKpiUseCase.CreateKpiCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new KpiIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public KpiResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<KpiResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public KpiIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateKpiRequest request) {
        var updated = updateUseCase.execute(new UpdateKpiUseCase.UpdateKpiCommand(
                null, request.title(), request.description(), null, id, null));
        return new KpiIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateKpiUseCase.ActivateKpiCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendKpiRequest request) {
        suspendUseCase.execute(new SuspendKpiUseCase.SuspendKpiCommand(
                null, null, null, null, id, request.reason()));
    }

    private KpiResponse toResponse(Kpi aggregate) {
        return new KpiResponse(
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

    public record CreateKpiRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateKpiRequest(String title, String description) {}

    public record SuspendKpiRequest(@NotBlank String reason) {}

    public record KpiIdResponse(String id) {}

    public record KpiResponse(
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
