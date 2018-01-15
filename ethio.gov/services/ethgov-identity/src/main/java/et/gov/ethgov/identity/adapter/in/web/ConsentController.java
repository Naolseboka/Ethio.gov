package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.consent.ActivateConsentUseCase;
import et.gov.ethgov.identity.application.consent.CreateConsentUseCase;
import et.gov.ethgov.identity.application.consent.GetConsentUseCase;
import et.gov.ethgov.identity.application.consent.ListConsentUseCase;
import et.gov.ethgov.identity.application.consent.SuspendConsentUseCase;
import et.gov.ethgov.identity.application.consent.UpdateConsentUseCase;
import et.gov.ethgov.identity.domain.consent.Consent;
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
@RequestMapping("/v1/identity/consent")
public class ConsentController {
    private final CreateConsentUseCase createUseCase;
    private final UpdateConsentUseCase updateUseCase;
    private final ActivateConsentUseCase activateUseCase;
    private final SuspendConsentUseCase suspendUseCase;
    private final GetConsentUseCase getUseCase;
    private final ListConsentUseCase listUseCase;

    public ConsentController(
            CreateConsentUseCase createUseCase,
            UpdateConsentUseCase updateUseCase,
            ActivateConsentUseCase activateUseCase,
            SuspendConsentUseCase suspendUseCase,
            GetConsentUseCase getUseCase,
            ListConsentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentIdResponse create(@Valid @RequestBody CreateConsentRequest request) {
        var id = createUseCase.execute(new CreateConsentUseCase.CreateConsentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ConsentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ConsentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ConsentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ConsentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateConsentRequest request) {
        var updated = updateUseCase.execute(new UpdateConsentUseCase.UpdateConsentCommand(
                null, request.title(), request.description(), null, id, null));
        return new ConsentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateConsentUseCase.ActivateConsentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendConsentRequest request) {
        suspendUseCase.execute(new SuspendConsentUseCase.SuspendConsentCommand(
                null, null, null, null, id, request.reason()));
    }

    private ConsentResponse toResponse(Consent aggregate) {
        return new ConsentResponse(
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

    public record CreateConsentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateConsentRequest(String title, String description) {}

    public record SuspendConsentRequest(@NotBlank String reason) {}

    public record ConsentIdResponse(String id) {}

    public record ConsentResponse(
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
