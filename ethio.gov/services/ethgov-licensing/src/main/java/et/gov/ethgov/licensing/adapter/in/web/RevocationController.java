package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.revocation.ActivateRevocationUseCase;
import et.gov.ethgov.licensing.application.revocation.CreateRevocationUseCase;
import et.gov.ethgov.licensing.application.revocation.GetRevocationUseCase;
import et.gov.ethgov.licensing.application.revocation.ListRevocationUseCase;
import et.gov.ethgov.licensing.application.revocation.SuspendRevocationUseCase;
import et.gov.ethgov.licensing.application.revocation.UpdateRevocationUseCase;
import et.gov.ethgov.licensing.domain.revocation.Revocation;
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
@RequestMapping("/v1/licensing/revocation")
public class RevocationController {
    private final CreateRevocationUseCase createUseCase;
    private final UpdateRevocationUseCase updateUseCase;
    private final ActivateRevocationUseCase activateUseCase;
    private final SuspendRevocationUseCase suspendUseCase;
    private final GetRevocationUseCase getUseCase;
    private final ListRevocationUseCase listUseCase;

    public RevocationController(
            CreateRevocationUseCase createUseCase,
            UpdateRevocationUseCase updateUseCase,
            ActivateRevocationUseCase activateUseCase,
            SuspendRevocationUseCase suspendUseCase,
            GetRevocationUseCase getUseCase,
            ListRevocationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RevocationIdResponse create(@Valid @RequestBody CreateRevocationRequest request) {
        var id = createUseCase.execute(new CreateRevocationUseCase.CreateRevocationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RevocationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RevocationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RevocationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RevocationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRevocationRequest request) {
        var updated = updateUseCase.execute(new UpdateRevocationUseCase.UpdateRevocationCommand(
                null, request.title(), request.description(), null, id, null));
        return new RevocationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRevocationUseCase.ActivateRevocationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRevocationRequest request) {
        suspendUseCase.execute(new SuspendRevocationUseCase.SuspendRevocationCommand(
                null, null, null, null, id, request.reason()));
    }

    private RevocationResponse toResponse(Revocation aggregate) {
        return new RevocationResponse(
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

    public record CreateRevocationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRevocationRequest(String title, String description) {}

    public record SuspendRevocationRequest(@NotBlank String reason) {}

    public record RevocationIdResponse(String id) {}

    public record RevocationResponse(
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
