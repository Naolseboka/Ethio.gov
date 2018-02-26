package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.license.ActivateLicenseUseCase;
import et.gov.ethgov.licensing.application.license.CreateLicenseUseCase;
import et.gov.ethgov.licensing.application.license.GetLicenseUseCase;
import et.gov.ethgov.licensing.application.license.ListLicenseUseCase;
import et.gov.ethgov.licensing.application.license.SuspendLicenseUseCase;
import et.gov.ethgov.licensing.application.license.UpdateLicenseUseCase;
import et.gov.ethgov.licensing.domain.license.License;
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
@RequestMapping("/v1/licensing/license")
public class LicenseController {
    private final CreateLicenseUseCase createUseCase;
    private final UpdateLicenseUseCase updateUseCase;
    private final ActivateLicenseUseCase activateUseCase;
    private final SuspendLicenseUseCase suspendUseCase;
    private final GetLicenseUseCase getUseCase;
    private final ListLicenseUseCase listUseCase;

    public LicenseController(
            CreateLicenseUseCase createUseCase,
            UpdateLicenseUseCase updateUseCase,
            ActivateLicenseUseCase activateUseCase,
            SuspendLicenseUseCase suspendUseCase,
            GetLicenseUseCase getUseCase,
            ListLicenseUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicenseIdResponse create(@Valid @RequestBody CreateLicenseRequest request) {
        var id = createUseCase.execute(new CreateLicenseUseCase.CreateLicenseCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new LicenseIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public LicenseResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<LicenseResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public LicenseIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateLicenseRequest request) {
        var updated = updateUseCase.execute(new UpdateLicenseUseCase.UpdateLicenseCommand(
                null, request.title(), request.description(), null, id, null));
        return new LicenseIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateLicenseUseCase.ActivateLicenseCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendLicenseRequest request) {
        suspendUseCase.execute(new SuspendLicenseUseCase.SuspendLicenseCommand(
                null, null, null, null, id, request.reason()));
    }

    private LicenseResponse toResponse(License aggregate) {
        return new LicenseResponse(
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

    public record CreateLicenseRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateLicenseRequest(String title, String description) {}

    public record SuspendLicenseRequest(@NotBlank String reason) {}

    public record LicenseIdResponse(String id) {}

    public record LicenseResponse(
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
