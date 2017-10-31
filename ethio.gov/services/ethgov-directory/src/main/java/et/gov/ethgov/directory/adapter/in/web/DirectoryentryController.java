package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.directoryentry.ActivateDirectoryentryUseCase;
import et.gov.ethgov.directory.application.directoryentry.CreateDirectoryentryUseCase;
import et.gov.ethgov.directory.application.directoryentry.GetDirectoryentryUseCase;
import et.gov.ethgov.directory.application.directoryentry.ListDirectoryentryUseCase;
import et.gov.ethgov.directory.application.directoryentry.SuspendDirectoryentryUseCase;
import et.gov.ethgov.directory.application.directoryentry.UpdateDirectoryentryUseCase;
import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
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
@RequestMapping("/v1/directory/directoryentry")
public class DirectoryentryController {
    private final CreateDirectoryentryUseCase createUseCase;
    private final UpdateDirectoryentryUseCase updateUseCase;
    private final ActivateDirectoryentryUseCase activateUseCase;
    private final SuspendDirectoryentryUseCase suspendUseCase;
    private final GetDirectoryentryUseCase getUseCase;
    private final ListDirectoryentryUseCase listUseCase;

    public DirectoryentryController(
            CreateDirectoryentryUseCase createUseCase,
            UpdateDirectoryentryUseCase updateUseCase,
            ActivateDirectoryentryUseCase activateUseCase,
            SuspendDirectoryentryUseCase suspendUseCase,
            GetDirectoryentryUseCase getUseCase,
            ListDirectoryentryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectoryentryIdResponse create(@Valid @RequestBody CreateDirectoryentryRequest request) {
        var id = createUseCase.execute(new CreateDirectoryentryUseCase.CreateDirectoryentryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DirectoryentryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DirectoryentryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DirectoryentryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DirectoryentryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDirectoryentryRequest request) {
        var updated = updateUseCase.execute(new UpdateDirectoryentryUseCase.UpdateDirectoryentryCommand(
                null, request.title(), request.description(), null, id, null));
        return new DirectoryentryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDirectoryentryUseCase.ActivateDirectoryentryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDirectoryentryRequest request) {
        suspendUseCase.execute(new SuspendDirectoryentryUseCase.SuspendDirectoryentryCommand(
                null, null, null, null, id, request.reason()));
    }

    private DirectoryentryResponse toResponse(Directoryentry aggregate) {
        return new DirectoryentryResponse(
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

    public record CreateDirectoryentryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDirectoryentryRequest(String title, String description) {}

    public record SuspendDirectoryentryRequest(@NotBlank String reason) {}

    public record DirectoryentryIdResponse(String id) {}

    public record DirectoryentryResponse(
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
