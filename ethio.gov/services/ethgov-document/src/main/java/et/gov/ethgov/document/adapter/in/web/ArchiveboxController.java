package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.archivebox.ActivateArchiveboxUseCase;
import et.gov.ethgov.document.application.archivebox.CreateArchiveboxUseCase;
import et.gov.ethgov.document.application.archivebox.GetArchiveboxUseCase;
import et.gov.ethgov.document.application.archivebox.ListArchiveboxUseCase;
import et.gov.ethgov.document.application.archivebox.SuspendArchiveboxUseCase;
import et.gov.ethgov.document.application.archivebox.UpdateArchiveboxUseCase;
import et.gov.ethgov.document.domain.archivebox.Archivebox;
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
@RequestMapping("/v1/document/archivebox")
public class ArchiveboxController {
    private final CreateArchiveboxUseCase createUseCase;
    private final UpdateArchiveboxUseCase updateUseCase;
    private final ActivateArchiveboxUseCase activateUseCase;
    private final SuspendArchiveboxUseCase suspendUseCase;
    private final GetArchiveboxUseCase getUseCase;
    private final ListArchiveboxUseCase listUseCase;

    public ArchiveboxController(
            CreateArchiveboxUseCase createUseCase,
            UpdateArchiveboxUseCase updateUseCase,
            ActivateArchiveboxUseCase activateUseCase,
            SuspendArchiveboxUseCase suspendUseCase,
            GetArchiveboxUseCase getUseCase,
            ListArchiveboxUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArchiveboxIdResponse create(@Valid @RequestBody CreateArchiveboxRequest request) {
        var id = createUseCase.execute(new CreateArchiveboxUseCase.CreateArchiveboxCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ArchiveboxIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ArchiveboxResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ArchiveboxResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ArchiveboxIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateArchiveboxRequest request) {
        var updated = updateUseCase.execute(new UpdateArchiveboxUseCase.UpdateArchiveboxCommand(
                null, request.title(), request.description(), null, id, null));
        return new ArchiveboxIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateArchiveboxUseCase.ActivateArchiveboxCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendArchiveboxRequest request) {
        suspendUseCase.execute(new SuspendArchiveboxUseCase.SuspendArchiveboxCommand(
                null, null, null, null, id, request.reason()));
    }

    private ArchiveboxResponse toResponse(Archivebox aggregate) {
        return new ArchiveboxResponse(
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

    public record CreateArchiveboxRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateArchiveboxRequest(String title, String description) {}

    public record SuspendArchiveboxRequest(@NotBlank String reason) {}

    public record ArchiveboxIdResponse(String id) {}

    public record ArchiveboxResponse(
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
