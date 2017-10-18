package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.usage.ActivateUsageUseCase;
import et.gov.ethgov.developer.application.usage.CreateUsageUseCase;
import et.gov.ethgov.developer.application.usage.GetUsageUseCase;
import et.gov.ethgov.developer.application.usage.ListUsageUseCase;
import et.gov.ethgov.developer.application.usage.SuspendUsageUseCase;
import et.gov.ethgov.developer.application.usage.UpdateUsageUseCase;
import et.gov.ethgov.developer.domain.usage.Usage;
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
@RequestMapping("/v1/developer/usage")
public class UsageController {
    private final CreateUsageUseCase createUseCase;
    private final UpdateUsageUseCase updateUseCase;
    private final ActivateUsageUseCase activateUseCase;
    private final SuspendUsageUseCase suspendUseCase;
    private final GetUsageUseCase getUseCase;
    private final ListUsageUseCase listUseCase;

    public UsageController(
            CreateUsageUseCase createUseCase,
            UpdateUsageUseCase updateUseCase,
            ActivateUsageUseCase activateUseCase,
            SuspendUsageUseCase suspendUseCase,
            GetUsageUseCase getUseCase,
            ListUsageUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsageIdResponse create(@Valid @RequestBody CreateUsageRequest request) {
        var id = createUseCase.execute(new CreateUsageUseCase.CreateUsageCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new UsageIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public UsageResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<UsageResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public UsageIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateUsageRequest request) {
        var updated = updateUseCase.execute(new UpdateUsageUseCase.UpdateUsageCommand(
                null, request.title(), request.description(), null, id, null));
        return new UsageIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateUsageUseCase.ActivateUsageCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendUsageRequest request) {
        suspendUseCase.execute(new SuspendUsageUseCase.SuspendUsageCommand(
                null, null, null, null, id, request.reason()));
    }

    private UsageResponse toResponse(Usage aggregate) {
        return new UsageResponse(
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

    public record CreateUsageRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateUsageRequest(String title, String description) {}

    public record SuspendUsageRequest(@NotBlank String reason) {}

    public record UsageIdResponse(String id) {}

    public record UsageResponse(
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
