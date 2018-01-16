package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.mfa.ActivateMfaUseCase;
import et.gov.ethgov.identity.application.mfa.CreateMfaUseCase;
import et.gov.ethgov.identity.application.mfa.GetMfaUseCase;
import et.gov.ethgov.identity.application.mfa.ListMfaUseCase;
import et.gov.ethgov.identity.application.mfa.SuspendMfaUseCase;
import et.gov.ethgov.identity.application.mfa.UpdateMfaUseCase;
import et.gov.ethgov.identity.domain.mfa.Mfa;
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
@RequestMapping("/v1/identity/mfa")
public class MfaController {
    private final CreateMfaUseCase createUseCase;
    private final UpdateMfaUseCase updateUseCase;
    private final ActivateMfaUseCase activateUseCase;
    private final SuspendMfaUseCase suspendUseCase;
    private final GetMfaUseCase getUseCase;
    private final ListMfaUseCase listUseCase;

    public MfaController(
            CreateMfaUseCase createUseCase,
            UpdateMfaUseCase updateUseCase,
            ActivateMfaUseCase activateUseCase,
            SuspendMfaUseCase suspendUseCase,
            GetMfaUseCase getUseCase,
            ListMfaUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MfaIdResponse create(@Valid @RequestBody CreateMfaRequest request) {
        var id = createUseCase.execute(new CreateMfaUseCase.CreateMfaCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MfaIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MfaResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MfaResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MfaIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMfaRequest request) {
        var updated = updateUseCase.execute(new UpdateMfaUseCase.UpdateMfaCommand(
                null, request.title(), request.description(), null, id, null));
        return new MfaIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMfaUseCase.ActivateMfaCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMfaRequest request) {
        suspendUseCase.execute(new SuspendMfaUseCase.SuspendMfaCommand(
                null, null, null, null, id, request.reason()));
    }

    private MfaResponse toResponse(Mfa aggregate) {
        return new MfaResponse(
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

    public record CreateMfaRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMfaRequest(String title, String description) {}

    public record SuspendMfaRequest(@NotBlank String reason) {}

    public record MfaIdResponse(String id) {}

    public record MfaResponse(
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
