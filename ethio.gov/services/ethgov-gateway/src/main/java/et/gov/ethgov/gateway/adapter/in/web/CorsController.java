package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.cors.ActivateCorsUseCase;
import et.gov.ethgov.gateway.application.cors.CreateCorsUseCase;
import et.gov.ethgov.gateway.application.cors.GetCorsUseCase;
import et.gov.ethgov.gateway.application.cors.ListCorsUseCase;
import et.gov.ethgov.gateway.application.cors.SuspendCorsUseCase;
import et.gov.ethgov.gateway.application.cors.UpdateCorsUseCase;
import et.gov.ethgov.gateway.domain.cors.Cors;
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
@RequestMapping("/v1/gateway/cors")
public class CorsController {
    private final CreateCorsUseCase createUseCase;
    private final UpdateCorsUseCase updateUseCase;
    private final ActivateCorsUseCase activateUseCase;
    private final SuspendCorsUseCase suspendUseCase;
    private final GetCorsUseCase getUseCase;
    private final ListCorsUseCase listUseCase;

    public CorsController(
            CreateCorsUseCase createUseCase,
            UpdateCorsUseCase updateUseCase,
            ActivateCorsUseCase activateUseCase,
            SuspendCorsUseCase suspendUseCase,
            GetCorsUseCase getUseCase,
            ListCorsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CorsIdResponse create(@Valid @RequestBody CreateCorsRequest request) {
        var id = createUseCase.execute(new CreateCorsUseCase.CreateCorsCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CorsIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CorsResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CorsResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CorsIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCorsRequest request) {
        var updated = updateUseCase.execute(new UpdateCorsUseCase.UpdateCorsCommand(
                null, request.title(), request.description(), null, id, null));
        return new CorsIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCorsUseCase.ActivateCorsCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCorsRequest request) {
        suspendUseCase.execute(new SuspendCorsUseCase.SuspendCorsCommand(
                null, null, null, null, id, request.reason()));
    }

    private CorsResponse toResponse(Cors aggregate) {
        return new CorsResponse(
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

    public record CreateCorsRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCorsRequest(String title, String description) {}

    public record SuspendCorsRequest(@NotBlank String reason) {}

    public record CorsIdResponse(String id) {}

    public record CorsResponse(
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
