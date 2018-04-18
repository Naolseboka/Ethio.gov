package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.seal.ActivateSealUseCase;
import et.gov.ethgov.organization.application.seal.CreateSealUseCase;
import et.gov.ethgov.organization.application.seal.GetSealUseCase;
import et.gov.ethgov.organization.application.seal.ListSealUseCase;
import et.gov.ethgov.organization.application.seal.SuspendSealUseCase;
import et.gov.ethgov.organization.application.seal.UpdateSealUseCase;
import et.gov.ethgov.organization.domain.seal.Seal;
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
@RequestMapping("/v1/organization/seal")
public class SealController {
    private final CreateSealUseCase createUseCase;
    private final UpdateSealUseCase updateUseCase;
    private final ActivateSealUseCase activateUseCase;
    private final SuspendSealUseCase suspendUseCase;
    private final GetSealUseCase getUseCase;
    private final ListSealUseCase listUseCase;

    public SealController(
            CreateSealUseCase createUseCase,
            UpdateSealUseCase updateUseCase,
            ActivateSealUseCase activateUseCase,
            SuspendSealUseCase suspendUseCase,
            GetSealUseCase getUseCase,
            ListSealUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SealIdResponse create(@Valid @RequestBody CreateSealRequest request) {
        var id = createUseCase.execute(new CreateSealUseCase.CreateSealCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SealIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SealResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SealResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SealIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSealRequest request) {
        var updated = updateUseCase.execute(new UpdateSealUseCase.UpdateSealCommand(
                null, request.title(), request.description(), null, id, null));
        return new SealIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSealUseCase.ActivateSealCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSealRequest request) {
        suspendUseCase.execute(new SuspendSealUseCase.SuspendSealCommand(
                null, null, null, null, id, request.reason()));
    }

    private SealResponse toResponse(Seal aggregate) {
        return new SealResponse(
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

    public record CreateSealRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSealRequest(String title, String description) {}

    public record SuspendSealRequest(@NotBlank String reason) {}

    public record SealIdResponse(String id) {}

    public record SealResponse(
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
