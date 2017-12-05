package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.authn.ActivateAuthnUseCase;
import et.gov.ethgov.gateway.application.authn.CreateAuthnUseCase;
import et.gov.ethgov.gateway.application.authn.GetAuthnUseCase;
import et.gov.ethgov.gateway.application.authn.ListAuthnUseCase;
import et.gov.ethgov.gateway.application.authn.SuspendAuthnUseCase;
import et.gov.ethgov.gateway.application.authn.UpdateAuthnUseCase;
import et.gov.ethgov.gateway.domain.authn.Authn;
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
@RequestMapping("/v1/gateway/authn")
public class AuthnController {
    private final CreateAuthnUseCase createUseCase;
    private final UpdateAuthnUseCase updateUseCase;
    private final ActivateAuthnUseCase activateUseCase;
    private final SuspendAuthnUseCase suspendUseCase;
    private final GetAuthnUseCase getUseCase;
    private final ListAuthnUseCase listUseCase;

    public AuthnController(
            CreateAuthnUseCase createUseCase,
            UpdateAuthnUseCase updateUseCase,
            ActivateAuthnUseCase activateUseCase,
            SuspendAuthnUseCase suspendUseCase,
            GetAuthnUseCase getUseCase,
            ListAuthnUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthnIdResponse create(@Valid @RequestBody CreateAuthnRequest request) {
        var id = createUseCase.execute(new CreateAuthnUseCase.CreateAuthnCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AuthnIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AuthnResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AuthnResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AuthnIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAuthnRequest request) {
        var updated = updateUseCase.execute(new UpdateAuthnUseCase.UpdateAuthnCommand(
                null, request.title(), request.description(), null, id, null));
        return new AuthnIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAuthnUseCase.ActivateAuthnCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAuthnRequest request) {
        suspendUseCase.execute(new SuspendAuthnUseCase.SuspendAuthnCommand(
                null, null, null, null, id, request.reason()));
    }

    private AuthnResponse toResponse(Authn aggregate) {
        return new AuthnResponse(
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

    public record CreateAuthnRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAuthnRequest(String title, String description) {}

    public record SuspendAuthnRequest(@NotBlank String reason) {}

    public record AuthnIdResponse(String id) {}

    public record AuthnResponse(
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
