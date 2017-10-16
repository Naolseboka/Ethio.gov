package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.devratelimit.ActivateDevratelimitUseCase;
import et.gov.ethgov.developer.application.devratelimit.CreateDevratelimitUseCase;
import et.gov.ethgov.developer.application.devratelimit.GetDevratelimitUseCase;
import et.gov.ethgov.developer.application.devratelimit.ListDevratelimitUseCase;
import et.gov.ethgov.developer.application.devratelimit.SuspendDevratelimitUseCase;
import et.gov.ethgov.developer.application.devratelimit.UpdateDevratelimitUseCase;
import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
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
@RequestMapping("/v1/developer/devratelimit")
public class DevratelimitController {
    private final CreateDevratelimitUseCase createUseCase;
    private final UpdateDevratelimitUseCase updateUseCase;
    private final ActivateDevratelimitUseCase activateUseCase;
    private final SuspendDevratelimitUseCase suspendUseCase;
    private final GetDevratelimitUseCase getUseCase;
    private final ListDevratelimitUseCase listUseCase;

    public DevratelimitController(
            CreateDevratelimitUseCase createUseCase,
            UpdateDevratelimitUseCase updateUseCase,
            ActivateDevratelimitUseCase activateUseCase,
            SuspendDevratelimitUseCase suspendUseCase,
            GetDevratelimitUseCase getUseCase,
            ListDevratelimitUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DevratelimitIdResponse create(@Valid @RequestBody CreateDevratelimitRequest request) {
        var id = createUseCase.execute(new CreateDevratelimitUseCase.CreateDevratelimitCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DevratelimitIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DevratelimitResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DevratelimitResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DevratelimitIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDevratelimitRequest request) {
        var updated = updateUseCase.execute(new UpdateDevratelimitUseCase.UpdateDevratelimitCommand(
                null, request.title(), request.description(), null, id, null));
        return new DevratelimitIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDevratelimitUseCase.ActivateDevratelimitCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDevratelimitRequest request) {
        suspendUseCase.execute(new SuspendDevratelimitUseCase.SuspendDevratelimitCommand(
                null, null, null, null, id, request.reason()));
    }

    private DevratelimitResponse toResponse(Devratelimit aggregate) {
        return new DevratelimitResponse(
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

    public record CreateDevratelimitRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDevratelimitRequest(String title, String description) {}

    public record SuspendDevratelimitRequest(@NotBlank String reason) {}

    public record DevratelimitIdResponse(String id) {}

    public record DevratelimitResponse(
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
