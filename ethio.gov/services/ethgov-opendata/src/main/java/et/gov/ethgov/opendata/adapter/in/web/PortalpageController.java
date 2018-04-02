package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.portalpage.ActivatePortalpageUseCase;
import et.gov.ethgov.opendata.application.portalpage.CreatePortalpageUseCase;
import et.gov.ethgov.opendata.application.portalpage.GetPortalpageUseCase;
import et.gov.ethgov.opendata.application.portalpage.ListPortalpageUseCase;
import et.gov.ethgov.opendata.application.portalpage.SuspendPortalpageUseCase;
import et.gov.ethgov.opendata.application.portalpage.UpdatePortalpageUseCase;
import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
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
@RequestMapping("/v1/opendata/portalpage")
public class PortalpageController {
    private final CreatePortalpageUseCase createUseCase;
    private final UpdatePortalpageUseCase updateUseCase;
    private final ActivatePortalpageUseCase activateUseCase;
    private final SuspendPortalpageUseCase suspendUseCase;
    private final GetPortalpageUseCase getUseCase;
    private final ListPortalpageUseCase listUseCase;

    public PortalpageController(
            CreatePortalpageUseCase createUseCase,
            UpdatePortalpageUseCase updateUseCase,
            ActivatePortalpageUseCase activateUseCase,
            SuspendPortalpageUseCase suspendUseCase,
            GetPortalpageUseCase getUseCase,
            ListPortalpageUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortalpageIdResponse create(@Valid @RequestBody CreatePortalpageRequest request) {
        var id = createUseCase.execute(new CreatePortalpageUseCase.CreatePortalpageCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PortalpageIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PortalpageResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PortalpageResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PortalpageIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePortalpageRequest request) {
        var updated = updateUseCase.execute(new UpdatePortalpageUseCase.UpdatePortalpageCommand(
                null, request.title(), request.description(), null, id, null));
        return new PortalpageIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePortalpageUseCase.ActivatePortalpageCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPortalpageRequest request) {
        suspendUseCase.execute(new SuspendPortalpageUseCase.SuspendPortalpageCommand(
                null, null, null, null, id, request.reason()));
    }

    private PortalpageResponse toResponse(Portalpage aggregate) {
        return new PortalpageResponse(
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

    public record CreatePortalpageRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePortalpageRequest(String title, String description) {}

    public record SuspendPortalpageRequest(@NotBlank String reason) {}

    public record PortalpageIdResponse(String id) {}

    public record PortalpageResponse(
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
