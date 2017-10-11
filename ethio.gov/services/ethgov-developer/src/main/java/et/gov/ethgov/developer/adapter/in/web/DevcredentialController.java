package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.devcredential.ActivateDevcredentialUseCase;
import et.gov.ethgov.developer.application.devcredential.CreateDevcredentialUseCase;
import et.gov.ethgov.developer.application.devcredential.GetDevcredentialUseCase;
import et.gov.ethgov.developer.application.devcredential.ListDevcredentialUseCase;
import et.gov.ethgov.developer.application.devcredential.SuspendDevcredentialUseCase;
import et.gov.ethgov.developer.application.devcredential.UpdateDevcredentialUseCase;
import et.gov.ethgov.developer.domain.devcredential.Devcredential;
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
@RequestMapping("/v1/developer/devcredential")
public class DevcredentialController {
    private final CreateDevcredentialUseCase createUseCase;
    private final UpdateDevcredentialUseCase updateUseCase;
    private final ActivateDevcredentialUseCase activateUseCase;
    private final SuspendDevcredentialUseCase suspendUseCase;
    private final GetDevcredentialUseCase getUseCase;
    private final ListDevcredentialUseCase listUseCase;

    public DevcredentialController(
            CreateDevcredentialUseCase createUseCase,
            UpdateDevcredentialUseCase updateUseCase,
            ActivateDevcredentialUseCase activateUseCase,
            SuspendDevcredentialUseCase suspendUseCase,
            GetDevcredentialUseCase getUseCase,
            ListDevcredentialUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DevcredentialIdResponse create(@Valid @RequestBody CreateDevcredentialRequest request) {
        var id = createUseCase.execute(new CreateDevcredentialUseCase.CreateDevcredentialCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DevcredentialIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DevcredentialResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DevcredentialResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DevcredentialIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDevcredentialRequest request) {
        var updated = updateUseCase.execute(new UpdateDevcredentialUseCase.UpdateDevcredentialCommand(
                null, request.title(), request.description(), null, id, null));
        return new DevcredentialIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDevcredentialUseCase.ActivateDevcredentialCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDevcredentialRequest request) {
        suspendUseCase.execute(new SuspendDevcredentialUseCase.SuspendDevcredentialCommand(
                null, null, null, null, id, request.reason()));
    }

    private DevcredentialResponse toResponse(Devcredential aggregate) {
        return new DevcredentialResponse(
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

    public record CreateDevcredentialRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDevcredentialRequest(String title, String description) {}

    public record SuspendDevcredentialRequest(@NotBlank String reason) {}

    public record DevcredentialIdResponse(String id) {}

    public record DevcredentialResponse(
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
