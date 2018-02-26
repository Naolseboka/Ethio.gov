package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.licensefee.ActivateLicensefeeUseCase;
import et.gov.ethgov.licensing.application.licensefee.CreateLicensefeeUseCase;
import et.gov.ethgov.licensing.application.licensefee.GetLicensefeeUseCase;
import et.gov.ethgov.licensing.application.licensefee.ListLicensefeeUseCase;
import et.gov.ethgov.licensing.application.licensefee.SuspendLicensefeeUseCase;
import et.gov.ethgov.licensing.application.licensefee.UpdateLicensefeeUseCase;
import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
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
@RequestMapping("/v1/licensing/licensefee")
public class LicensefeeController {
    private final CreateLicensefeeUseCase createUseCase;
    private final UpdateLicensefeeUseCase updateUseCase;
    private final ActivateLicensefeeUseCase activateUseCase;
    private final SuspendLicensefeeUseCase suspendUseCase;
    private final GetLicensefeeUseCase getUseCase;
    private final ListLicensefeeUseCase listUseCase;

    public LicensefeeController(
            CreateLicensefeeUseCase createUseCase,
            UpdateLicensefeeUseCase updateUseCase,
            ActivateLicensefeeUseCase activateUseCase,
            SuspendLicensefeeUseCase suspendUseCase,
            GetLicensefeeUseCase getUseCase,
            ListLicensefeeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicensefeeIdResponse create(@Valid @RequestBody CreateLicensefeeRequest request) {
        var id = createUseCase.execute(new CreateLicensefeeUseCase.CreateLicensefeeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new LicensefeeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public LicensefeeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<LicensefeeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public LicensefeeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateLicensefeeRequest request) {
        var updated = updateUseCase.execute(new UpdateLicensefeeUseCase.UpdateLicensefeeCommand(
                null, request.title(), request.description(), null, id, null));
        return new LicensefeeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateLicensefeeUseCase.ActivateLicensefeeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendLicensefeeRequest request) {
        suspendUseCase.execute(new SuspendLicensefeeUseCase.SuspendLicensefeeCommand(
                null, null, null, null, id, request.reason()));
    }

    private LicensefeeResponse toResponse(Licensefee aggregate) {
        return new LicensefeeResponse(
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

    public record CreateLicensefeeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateLicensefeeRequest(String title, String description) {}

    public record SuspendLicensefeeRequest(@NotBlank String reason) {}

    public record LicensefeeIdResponse(String id) {}

    public record LicensefeeResponse(
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
