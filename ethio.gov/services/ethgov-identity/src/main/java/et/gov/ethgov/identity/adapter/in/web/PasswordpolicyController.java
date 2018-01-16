package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.passwordpolicy.ActivatePasswordpolicyUseCase;
import et.gov.ethgov.identity.application.passwordpolicy.CreatePasswordpolicyUseCase;
import et.gov.ethgov.identity.application.passwordpolicy.GetPasswordpolicyUseCase;
import et.gov.ethgov.identity.application.passwordpolicy.ListPasswordpolicyUseCase;
import et.gov.ethgov.identity.application.passwordpolicy.SuspendPasswordpolicyUseCase;
import et.gov.ethgov.identity.application.passwordpolicy.UpdatePasswordpolicyUseCase;
import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
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
@RequestMapping("/v1/identity/passwordpolicy")
public class PasswordpolicyController {
    private final CreatePasswordpolicyUseCase createUseCase;
    private final UpdatePasswordpolicyUseCase updateUseCase;
    private final ActivatePasswordpolicyUseCase activateUseCase;
    private final SuspendPasswordpolicyUseCase suspendUseCase;
    private final GetPasswordpolicyUseCase getUseCase;
    private final ListPasswordpolicyUseCase listUseCase;

    public PasswordpolicyController(
            CreatePasswordpolicyUseCase createUseCase,
            UpdatePasswordpolicyUseCase updateUseCase,
            ActivatePasswordpolicyUseCase activateUseCase,
            SuspendPasswordpolicyUseCase suspendUseCase,
            GetPasswordpolicyUseCase getUseCase,
            ListPasswordpolicyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PasswordpolicyIdResponse create(@Valid @RequestBody CreatePasswordpolicyRequest request) {
        var id = createUseCase.execute(new CreatePasswordpolicyUseCase.CreatePasswordpolicyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PasswordpolicyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PasswordpolicyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PasswordpolicyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PasswordpolicyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePasswordpolicyRequest request) {
        var updated = updateUseCase.execute(new UpdatePasswordpolicyUseCase.UpdatePasswordpolicyCommand(
                null, request.title(), request.description(), null, id, null));
        return new PasswordpolicyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePasswordpolicyUseCase.ActivatePasswordpolicyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPasswordpolicyRequest request) {
        suspendUseCase.execute(new SuspendPasswordpolicyUseCase.SuspendPasswordpolicyCommand(
                null, null, null, null, id, request.reason()));
    }

    private PasswordpolicyResponse toResponse(Passwordpolicy aggregate) {
        return new PasswordpolicyResponse(
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

    public record CreatePasswordpolicyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePasswordpolicyRequest(String title, String description) {}

    public record SuspendPasswordpolicyRequest(@NotBlank String reason) {}

    public record PasswordpolicyIdResponse(String id) {}

    public record PasswordpolicyResponse(
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
