package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.boost.ActivateBoostUseCase;
import et.gov.ethgov.search.application.boost.CreateBoostUseCase;
import et.gov.ethgov.search.application.boost.GetBoostUseCase;
import et.gov.ethgov.search.application.boost.ListBoostUseCase;
import et.gov.ethgov.search.application.boost.SuspendBoostUseCase;
import et.gov.ethgov.search.application.boost.UpdateBoostUseCase;
import et.gov.ethgov.search.domain.boost.Boost;
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
@RequestMapping("/v1/search/boost")
public class BoostController {
    private final CreateBoostUseCase createUseCase;
    private final UpdateBoostUseCase updateUseCase;
    private final ActivateBoostUseCase activateUseCase;
    private final SuspendBoostUseCase suspendUseCase;
    private final GetBoostUseCase getUseCase;
    private final ListBoostUseCase listUseCase;

    public BoostController(
            CreateBoostUseCase createUseCase,
            UpdateBoostUseCase updateUseCase,
            ActivateBoostUseCase activateUseCase,
            SuspendBoostUseCase suspendUseCase,
            GetBoostUseCase getUseCase,
            ListBoostUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoostIdResponse create(@Valid @RequestBody CreateBoostRequest request) {
        var id = createUseCase.execute(new CreateBoostUseCase.CreateBoostCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BoostIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BoostResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BoostResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BoostIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBoostRequest request) {
        var updated = updateUseCase.execute(new UpdateBoostUseCase.UpdateBoostCommand(
                null, request.title(), request.description(), null, id, null));
        return new BoostIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBoostUseCase.ActivateBoostCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBoostRequest request) {
        suspendUseCase.execute(new SuspendBoostUseCase.SuspendBoostCommand(
                null, null, null, null, id, request.reason()));
    }

    private BoostResponse toResponse(Boost aggregate) {
        return new BoostResponse(
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

    public record CreateBoostRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBoostRequest(String title, String description) {}

    public record SuspendBoostRequest(@NotBlank String reason) {}

    public record BoostIdResponse(String id) {}

    public record BoostResponse(
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
