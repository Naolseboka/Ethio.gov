package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.valuation.ActivateValuationUseCase;
import et.gov.ethgov.land.application.valuation.CreateValuationUseCase;
import et.gov.ethgov.land.application.valuation.GetValuationUseCase;
import et.gov.ethgov.land.application.valuation.ListValuationUseCase;
import et.gov.ethgov.land.application.valuation.SuspendValuationUseCase;
import et.gov.ethgov.land.application.valuation.UpdateValuationUseCase;
import et.gov.ethgov.land.domain.valuation.Valuation;
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
@RequestMapping("/v1/land/valuation")
public class ValuationController {
    private final CreateValuationUseCase createUseCase;
    private final UpdateValuationUseCase updateUseCase;
    private final ActivateValuationUseCase activateUseCase;
    private final SuspendValuationUseCase suspendUseCase;
    private final GetValuationUseCase getUseCase;
    private final ListValuationUseCase listUseCase;

    public ValuationController(
            CreateValuationUseCase createUseCase,
            UpdateValuationUseCase updateUseCase,
            ActivateValuationUseCase activateUseCase,
            SuspendValuationUseCase suspendUseCase,
            GetValuationUseCase getUseCase,
            ListValuationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ValuationIdResponse create(@Valid @RequestBody CreateValuationRequest request) {
        var id = createUseCase.execute(new CreateValuationUseCase.CreateValuationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ValuationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ValuationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ValuationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ValuationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateValuationRequest request) {
        var updated = updateUseCase.execute(new UpdateValuationUseCase.UpdateValuationCommand(
                null, request.title(), request.description(), null, id, null));
        return new ValuationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateValuationUseCase.ActivateValuationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendValuationRequest request) {
        suspendUseCase.execute(new SuspendValuationUseCase.SuspendValuationCommand(
                null, null, null, null, id, request.reason()));
    }

    private ValuationResponse toResponse(Valuation aggregate) {
        return new ValuationResponse(
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

    public record CreateValuationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateValuationRequest(String title, String description) {}

    public record SuspendValuationRequest(@NotBlank String reason) {}

    public record ValuationIdResponse(String id) {}

    public record ValuationResponse(
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
