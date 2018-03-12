package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.preference.ActivatePreferenceUseCase;
import et.gov.ethgov.notification.application.preference.CreatePreferenceUseCase;
import et.gov.ethgov.notification.application.preference.GetPreferenceUseCase;
import et.gov.ethgov.notification.application.preference.ListPreferenceUseCase;
import et.gov.ethgov.notification.application.preference.SuspendPreferenceUseCase;
import et.gov.ethgov.notification.application.preference.UpdatePreferenceUseCase;
import et.gov.ethgov.notification.domain.preference.Preference;
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
@RequestMapping("/v1/notification/preference")
public class PreferenceController {
    private final CreatePreferenceUseCase createUseCase;
    private final UpdatePreferenceUseCase updateUseCase;
    private final ActivatePreferenceUseCase activateUseCase;
    private final SuspendPreferenceUseCase suspendUseCase;
    private final GetPreferenceUseCase getUseCase;
    private final ListPreferenceUseCase listUseCase;

    public PreferenceController(
            CreatePreferenceUseCase createUseCase,
            UpdatePreferenceUseCase updateUseCase,
            ActivatePreferenceUseCase activateUseCase,
            SuspendPreferenceUseCase suspendUseCase,
            GetPreferenceUseCase getUseCase,
            ListPreferenceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreferenceIdResponse create(@Valid @RequestBody CreatePreferenceRequest request) {
        var id = createUseCase.execute(new CreatePreferenceUseCase.CreatePreferenceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PreferenceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PreferenceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PreferenceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PreferenceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePreferenceRequest request) {
        var updated = updateUseCase.execute(new UpdatePreferenceUseCase.UpdatePreferenceCommand(
                null, request.title(), request.description(), null, id, null));
        return new PreferenceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePreferenceUseCase.ActivatePreferenceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPreferenceRequest request) {
        suspendUseCase.execute(new SuspendPreferenceUseCase.SuspendPreferenceCommand(
                null, null, null, null, id, request.reason()));
    }

    private PreferenceResponse toResponse(Preference aggregate) {
        return new PreferenceResponse(
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

    public record CreatePreferenceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePreferenceRequest(String title, String description) {}

    public record SuspendPreferenceRequest(@NotBlank String reason) {}

    public record PreferenceIdResponse(String id) {}

    public record PreferenceResponse(
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
