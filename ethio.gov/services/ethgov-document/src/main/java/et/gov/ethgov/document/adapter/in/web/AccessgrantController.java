package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.accessgrant.ActivateAccessgrantUseCase;
import et.gov.ethgov.document.application.accessgrant.CreateAccessgrantUseCase;
import et.gov.ethgov.document.application.accessgrant.GetAccessgrantUseCase;
import et.gov.ethgov.document.application.accessgrant.ListAccessgrantUseCase;
import et.gov.ethgov.document.application.accessgrant.SuspendAccessgrantUseCase;
import et.gov.ethgov.document.application.accessgrant.UpdateAccessgrantUseCase;
import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
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
@RequestMapping("/v1/document/accessgrant")
public class AccessgrantController {
    private final CreateAccessgrantUseCase createUseCase;
    private final UpdateAccessgrantUseCase updateUseCase;
    private final ActivateAccessgrantUseCase activateUseCase;
    private final SuspendAccessgrantUseCase suspendUseCase;
    private final GetAccessgrantUseCase getUseCase;
    private final ListAccessgrantUseCase listUseCase;

    public AccessgrantController(
            CreateAccessgrantUseCase createUseCase,
            UpdateAccessgrantUseCase updateUseCase,
            ActivateAccessgrantUseCase activateUseCase,
            SuspendAccessgrantUseCase suspendUseCase,
            GetAccessgrantUseCase getUseCase,
            ListAccessgrantUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccessgrantIdResponse create(@Valid @RequestBody CreateAccessgrantRequest request) {
        var id = createUseCase.execute(new CreateAccessgrantUseCase.CreateAccessgrantCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AccessgrantIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AccessgrantResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AccessgrantResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AccessgrantIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAccessgrantRequest request) {
        var updated = updateUseCase.execute(new UpdateAccessgrantUseCase.UpdateAccessgrantCommand(
                null, request.title(), request.description(), null, id, null));
        return new AccessgrantIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAccessgrantUseCase.ActivateAccessgrantCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAccessgrantRequest request) {
        suspendUseCase.execute(new SuspendAccessgrantUseCase.SuspendAccessgrantCommand(
                null, null, null, null, id, request.reason()));
    }

    private AccessgrantResponse toResponse(Accessgrant aggregate) {
        return new AccessgrantResponse(
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

    public record CreateAccessgrantRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAccessgrantRequest(String title, String description) {}

    public record SuspendAccessgrantRequest(@NotBlank String reason) {}

    public record AccessgrantIdResponse(String id) {}

    public record AccessgrantResponse(
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
