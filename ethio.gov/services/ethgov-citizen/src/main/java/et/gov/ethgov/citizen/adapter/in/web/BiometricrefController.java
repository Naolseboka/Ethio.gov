package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.biometricref.ActivateBiometricrefUseCase;
import et.gov.ethgov.citizen.application.biometricref.CreateBiometricrefUseCase;
import et.gov.ethgov.citizen.application.biometricref.GetBiometricrefUseCase;
import et.gov.ethgov.citizen.application.biometricref.ListBiometricrefUseCase;
import et.gov.ethgov.citizen.application.biometricref.SuspendBiometricrefUseCase;
import et.gov.ethgov.citizen.application.biometricref.UpdateBiometricrefUseCase;
import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
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
@RequestMapping("/v1/citizen/biometricref")
public class BiometricrefController {
    private final CreateBiometricrefUseCase createUseCase;
    private final UpdateBiometricrefUseCase updateUseCase;
    private final ActivateBiometricrefUseCase activateUseCase;
    private final SuspendBiometricrefUseCase suspendUseCase;
    private final GetBiometricrefUseCase getUseCase;
    private final ListBiometricrefUseCase listUseCase;

    public BiometricrefController(
            CreateBiometricrefUseCase createUseCase,
            UpdateBiometricrefUseCase updateUseCase,
            ActivateBiometricrefUseCase activateUseCase,
            SuspendBiometricrefUseCase suspendUseCase,
            GetBiometricrefUseCase getUseCase,
            ListBiometricrefUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BiometricrefIdResponse create(@Valid @RequestBody CreateBiometricrefRequest request) {
        var id = createUseCase.execute(new CreateBiometricrefUseCase.CreateBiometricrefCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BiometricrefIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BiometricrefResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BiometricrefResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BiometricrefIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBiometricrefRequest request) {
        var updated = updateUseCase.execute(new UpdateBiometricrefUseCase.UpdateBiometricrefCommand(
                null, request.title(), request.description(), null, id, null));
        return new BiometricrefIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBiometricrefUseCase.ActivateBiometricrefCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBiometricrefRequest request) {
        suspendUseCase.execute(new SuspendBiometricrefUseCase.SuspendBiometricrefCommand(
                null, null, null, null, id, request.reason()));
    }

    private BiometricrefResponse toResponse(Biometricref aggregate) {
        return new BiometricrefResponse(
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

    public record CreateBiometricrefRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBiometricrefRequest(String title, String description) {}

    public record SuspendBiometricrefRequest(@NotBlank String reason) {}

    public record BiometricrefIdResponse(String id) {}

    public record BiometricrefResponse(
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
