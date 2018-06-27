package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.exemption.ActivateExemptionUseCase;
import et.gov.ethgov.revenue.application.exemption.CreateExemptionUseCase;
import et.gov.ethgov.revenue.application.exemption.GetExemptionUseCase;
import et.gov.ethgov.revenue.application.exemption.ListExemptionUseCase;
import et.gov.ethgov.revenue.application.exemption.SuspendExemptionUseCase;
import et.gov.ethgov.revenue.application.exemption.UpdateExemptionUseCase;
import et.gov.ethgov.revenue.domain.exemption.Exemption;
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
@RequestMapping("/v1/revenue/exemption")
public class ExemptionController {
    private final CreateExemptionUseCase createUseCase;
    private final UpdateExemptionUseCase updateUseCase;
    private final ActivateExemptionUseCase activateUseCase;
    private final SuspendExemptionUseCase suspendUseCase;
    private final GetExemptionUseCase getUseCase;
    private final ListExemptionUseCase listUseCase;

    public ExemptionController(
            CreateExemptionUseCase createUseCase,
            UpdateExemptionUseCase updateUseCase,
            ActivateExemptionUseCase activateUseCase,
            SuspendExemptionUseCase suspendUseCase,
            GetExemptionUseCase getUseCase,
            ListExemptionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExemptionIdResponse create(@Valid @RequestBody CreateExemptionRequest request) {
        var id = createUseCase.execute(new CreateExemptionUseCase.CreateExemptionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ExemptionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ExemptionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ExemptionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ExemptionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateExemptionRequest request) {
        var updated = updateUseCase.execute(new UpdateExemptionUseCase.UpdateExemptionCommand(
                null, request.title(), request.description(), null, id, null));
        return new ExemptionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateExemptionUseCase.ActivateExemptionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendExemptionRequest request) {
        suspendUseCase.execute(new SuspendExemptionUseCase.SuspendExemptionCommand(
                null, null, null, null, id, request.reason()));
    }

    private ExemptionResponse toResponse(Exemption aggregate) {
        return new ExemptionResponse(
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

    public record CreateExemptionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateExemptionRequest(String title, String description) {}

    public record SuspendExemptionRequest(@NotBlank String reason) {}

    public record ExemptionIdResponse(String id) {}

    public record ExemptionResponse(
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
