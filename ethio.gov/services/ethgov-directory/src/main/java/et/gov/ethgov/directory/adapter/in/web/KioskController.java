package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.kiosk.ActivateKioskUseCase;
import et.gov.ethgov.directory.application.kiosk.CreateKioskUseCase;
import et.gov.ethgov.directory.application.kiosk.GetKioskUseCase;
import et.gov.ethgov.directory.application.kiosk.ListKioskUseCase;
import et.gov.ethgov.directory.application.kiosk.SuspendKioskUseCase;
import et.gov.ethgov.directory.application.kiosk.UpdateKioskUseCase;
import et.gov.ethgov.directory.domain.kiosk.Kiosk;
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
@RequestMapping("/v1/directory/kiosk")
public class KioskController {
    private final CreateKioskUseCase createUseCase;
    private final UpdateKioskUseCase updateUseCase;
    private final ActivateKioskUseCase activateUseCase;
    private final SuspendKioskUseCase suspendUseCase;
    private final GetKioskUseCase getUseCase;
    private final ListKioskUseCase listUseCase;

    public KioskController(
            CreateKioskUseCase createUseCase,
            UpdateKioskUseCase updateUseCase,
            ActivateKioskUseCase activateUseCase,
            SuspendKioskUseCase suspendUseCase,
            GetKioskUseCase getUseCase,
            ListKioskUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KioskIdResponse create(@Valid @RequestBody CreateKioskRequest request) {
        var id = createUseCase.execute(new CreateKioskUseCase.CreateKioskCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new KioskIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public KioskResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<KioskResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public KioskIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateKioskRequest request) {
        var updated = updateUseCase.execute(new UpdateKioskUseCase.UpdateKioskCommand(
                null, request.title(), request.description(), null, id, null));
        return new KioskIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateKioskUseCase.ActivateKioskCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendKioskRequest request) {
        suspendUseCase.execute(new SuspendKioskUseCase.SuspendKioskCommand(
                null, null, null, null, id, request.reason()));
    }

    private KioskResponse toResponse(Kiosk aggregate) {
        return new KioskResponse(
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

    public record CreateKioskRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateKioskRequest(String title, String description) {}

    public record SuspendKioskRequest(@NotBlank String reason) {}

    public record KioskIdResponse(String id) {}

    public record KioskResponse(
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
