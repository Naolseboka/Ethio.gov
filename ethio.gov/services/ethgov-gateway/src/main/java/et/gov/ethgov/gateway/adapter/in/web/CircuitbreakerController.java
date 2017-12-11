package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.circuitbreaker.ActivateCircuitbreakerUseCase;
import et.gov.ethgov.gateway.application.circuitbreaker.CreateCircuitbreakerUseCase;
import et.gov.ethgov.gateway.application.circuitbreaker.GetCircuitbreakerUseCase;
import et.gov.ethgov.gateway.application.circuitbreaker.ListCircuitbreakerUseCase;
import et.gov.ethgov.gateway.application.circuitbreaker.SuspendCircuitbreakerUseCase;
import et.gov.ethgov.gateway.application.circuitbreaker.UpdateCircuitbreakerUseCase;
import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
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
@RequestMapping("/v1/gateway/circuitbreaker")
public class CircuitbreakerController {
    private final CreateCircuitbreakerUseCase createUseCase;
    private final UpdateCircuitbreakerUseCase updateUseCase;
    private final ActivateCircuitbreakerUseCase activateUseCase;
    private final SuspendCircuitbreakerUseCase suspendUseCase;
    private final GetCircuitbreakerUseCase getUseCase;
    private final ListCircuitbreakerUseCase listUseCase;

    public CircuitbreakerController(
            CreateCircuitbreakerUseCase createUseCase,
            UpdateCircuitbreakerUseCase updateUseCase,
            ActivateCircuitbreakerUseCase activateUseCase,
            SuspendCircuitbreakerUseCase suspendUseCase,
            GetCircuitbreakerUseCase getUseCase,
            ListCircuitbreakerUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CircuitbreakerIdResponse create(@Valid @RequestBody CreateCircuitbreakerRequest request) {
        var id = createUseCase.execute(new CreateCircuitbreakerUseCase.CreateCircuitbreakerCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CircuitbreakerIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CircuitbreakerResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CircuitbreakerResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CircuitbreakerIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCircuitbreakerRequest request) {
        var updated = updateUseCase.execute(new UpdateCircuitbreakerUseCase.UpdateCircuitbreakerCommand(
                null, request.title(), request.description(), null, id, null));
        return new CircuitbreakerIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCircuitbreakerUseCase.ActivateCircuitbreakerCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCircuitbreakerRequest request) {
        suspendUseCase.execute(new SuspendCircuitbreakerUseCase.SuspendCircuitbreakerCommand(
                null, null, null, null, id, request.reason()));
    }

    private CircuitbreakerResponse toResponse(Circuitbreaker aggregate) {
        return new CircuitbreakerResponse(
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

    public record CreateCircuitbreakerRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCircuitbreakerRequest(String title, String description) {}

    public record SuspendCircuitbreakerRequest(@NotBlank String reason) {}

    public record CircuitbreakerIdResponse(String id) {}

    public record CircuitbreakerResponse(
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
