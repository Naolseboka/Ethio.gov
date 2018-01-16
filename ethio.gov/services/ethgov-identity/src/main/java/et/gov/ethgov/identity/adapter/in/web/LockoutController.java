package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.lockout.ActivateLockoutUseCase;
import et.gov.ethgov.identity.application.lockout.CreateLockoutUseCase;
import et.gov.ethgov.identity.application.lockout.GetLockoutUseCase;
import et.gov.ethgov.identity.application.lockout.ListLockoutUseCase;
import et.gov.ethgov.identity.application.lockout.SuspendLockoutUseCase;
import et.gov.ethgov.identity.application.lockout.UpdateLockoutUseCase;
import et.gov.ethgov.identity.domain.lockout.Lockout;
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
@RequestMapping("/v1/identity/lockout")
public class LockoutController {
    private final CreateLockoutUseCase createUseCase;
    private final UpdateLockoutUseCase updateUseCase;
    private final ActivateLockoutUseCase activateUseCase;
    private final SuspendLockoutUseCase suspendUseCase;
    private final GetLockoutUseCase getUseCase;
    private final ListLockoutUseCase listUseCase;

    public LockoutController(
            CreateLockoutUseCase createUseCase,
            UpdateLockoutUseCase updateUseCase,
            ActivateLockoutUseCase activateUseCase,
            SuspendLockoutUseCase suspendUseCase,
            GetLockoutUseCase getUseCase,
            ListLockoutUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LockoutIdResponse create(@Valid @RequestBody CreateLockoutRequest request) {
        var id = createUseCase.execute(new CreateLockoutUseCase.CreateLockoutCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new LockoutIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public LockoutResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<LockoutResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public LockoutIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateLockoutRequest request) {
        var updated = updateUseCase.execute(new UpdateLockoutUseCase.UpdateLockoutCommand(
                null, request.title(), request.description(), null, id, null));
        return new LockoutIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateLockoutUseCase.ActivateLockoutCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendLockoutRequest request) {
        suspendUseCase.execute(new SuspendLockoutUseCase.SuspendLockoutCommand(
                null, null, null, null, id, request.reason()));
    }

    private LockoutResponse toResponse(Lockout aggregate) {
        return new LockoutResponse(
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

    public record CreateLockoutRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateLockoutRequest(String title, String description) {}

    public record SuspendLockoutRequest(@NotBlank String reason) {}

    public record LockoutIdResponse(String id) {}

    public record LockoutResponse(
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
