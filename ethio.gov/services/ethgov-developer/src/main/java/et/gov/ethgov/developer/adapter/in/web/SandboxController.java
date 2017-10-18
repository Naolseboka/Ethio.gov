package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.sandbox.ActivateSandboxUseCase;
import et.gov.ethgov.developer.application.sandbox.CreateSandboxUseCase;
import et.gov.ethgov.developer.application.sandbox.GetSandboxUseCase;
import et.gov.ethgov.developer.application.sandbox.ListSandboxUseCase;
import et.gov.ethgov.developer.application.sandbox.SuspendSandboxUseCase;
import et.gov.ethgov.developer.application.sandbox.UpdateSandboxUseCase;
import et.gov.ethgov.developer.domain.sandbox.Sandbox;
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
@RequestMapping("/v1/developer/sandbox")
public class SandboxController {
    private final CreateSandboxUseCase createUseCase;
    private final UpdateSandboxUseCase updateUseCase;
    private final ActivateSandboxUseCase activateUseCase;
    private final SuspendSandboxUseCase suspendUseCase;
    private final GetSandboxUseCase getUseCase;
    private final ListSandboxUseCase listUseCase;

    public SandboxController(
            CreateSandboxUseCase createUseCase,
            UpdateSandboxUseCase updateUseCase,
            ActivateSandboxUseCase activateUseCase,
            SuspendSandboxUseCase suspendUseCase,
            GetSandboxUseCase getUseCase,
            ListSandboxUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SandboxIdResponse create(@Valid @RequestBody CreateSandboxRequest request) {
        var id = createUseCase.execute(new CreateSandboxUseCase.CreateSandboxCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SandboxIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SandboxResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SandboxResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SandboxIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSandboxRequest request) {
        var updated = updateUseCase.execute(new UpdateSandboxUseCase.UpdateSandboxCommand(
                null, request.title(), request.description(), null, id, null));
        return new SandboxIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSandboxUseCase.ActivateSandboxCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSandboxRequest request) {
        suspendUseCase.execute(new SuspendSandboxUseCase.SuspendSandboxCommand(
                null, null, null, null, id, request.reason()));
    }

    private SandboxResponse toResponse(Sandbox aggregate) {
        return new SandboxResponse(
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

    public record CreateSandboxRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSandboxRequest(String title, String description) {}

    public record SuspendSandboxRequest(@NotBlank String reason) {}

    public record SandboxIdResponse(String id) {}

    public record SandboxResponse(
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
