package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.portaluser.ActivatePortaluserUseCase;
import et.gov.ethgov.developer.application.portaluser.CreatePortaluserUseCase;
import et.gov.ethgov.developer.application.portaluser.GetPortaluserUseCase;
import et.gov.ethgov.developer.application.portaluser.ListPortaluserUseCase;
import et.gov.ethgov.developer.application.portaluser.SuspendPortaluserUseCase;
import et.gov.ethgov.developer.application.portaluser.UpdatePortaluserUseCase;
import et.gov.ethgov.developer.domain.portaluser.Portaluser;
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
@RequestMapping("/v1/developer/portaluser")
public class PortaluserController {
    private final CreatePortaluserUseCase createUseCase;
    private final UpdatePortaluserUseCase updateUseCase;
    private final ActivatePortaluserUseCase activateUseCase;
    private final SuspendPortaluserUseCase suspendUseCase;
    private final GetPortaluserUseCase getUseCase;
    private final ListPortaluserUseCase listUseCase;

    public PortaluserController(
            CreatePortaluserUseCase createUseCase,
            UpdatePortaluserUseCase updateUseCase,
            ActivatePortaluserUseCase activateUseCase,
            SuspendPortaluserUseCase suspendUseCase,
            GetPortaluserUseCase getUseCase,
            ListPortaluserUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortaluserIdResponse create(@Valid @RequestBody CreatePortaluserRequest request) {
        var id = createUseCase.execute(new CreatePortaluserUseCase.CreatePortaluserCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PortaluserIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PortaluserResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PortaluserResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PortaluserIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePortaluserRequest request) {
        var updated = updateUseCase.execute(new UpdatePortaluserUseCase.UpdatePortaluserCommand(
                null, request.title(), request.description(), null, id, null));
        return new PortaluserIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePortaluserUseCase.ActivatePortaluserCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPortaluserRequest request) {
        suspendUseCase.execute(new SuspendPortaluserUseCase.SuspendPortaluserCommand(
                null, null, null, null, id, request.reason()));
    }

    private PortaluserResponse toResponse(Portaluser aggregate) {
        return new PortaluserResponse(
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

    public record CreatePortaluserRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePortaluserRequest(String title, String description) {}

    public record SuspendPortaluserRequest(@NotBlank String reason) {}

    public record PortaluserIdResponse(String id) {}

    public record PortaluserResponse(
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
