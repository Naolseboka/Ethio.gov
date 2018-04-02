package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.download.ActivateDownloadUseCase;
import et.gov.ethgov.opendata.application.download.CreateDownloadUseCase;
import et.gov.ethgov.opendata.application.download.GetDownloadUseCase;
import et.gov.ethgov.opendata.application.download.ListDownloadUseCase;
import et.gov.ethgov.opendata.application.download.SuspendDownloadUseCase;
import et.gov.ethgov.opendata.application.download.UpdateDownloadUseCase;
import et.gov.ethgov.opendata.domain.download.Download;
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
@RequestMapping("/v1/opendata/download")
public class DownloadController {
    private final CreateDownloadUseCase createUseCase;
    private final UpdateDownloadUseCase updateUseCase;
    private final ActivateDownloadUseCase activateUseCase;
    private final SuspendDownloadUseCase suspendUseCase;
    private final GetDownloadUseCase getUseCase;
    private final ListDownloadUseCase listUseCase;

    public DownloadController(
            CreateDownloadUseCase createUseCase,
            UpdateDownloadUseCase updateUseCase,
            ActivateDownloadUseCase activateUseCase,
            SuspendDownloadUseCase suspendUseCase,
            GetDownloadUseCase getUseCase,
            ListDownloadUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DownloadIdResponse create(@Valid @RequestBody CreateDownloadRequest request) {
        var id = createUseCase.execute(new CreateDownloadUseCase.CreateDownloadCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DownloadIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DownloadResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DownloadResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DownloadIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDownloadRequest request) {
        var updated = updateUseCase.execute(new UpdateDownloadUseCase.UpdateDownloadCommand(
                null, request.title(), request.description(), null, id, null));
        return new DownloadIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDownloadUseCase.ActivateDownloadCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDownloadRequest request) {
        suspendUseCase.execute(new SuspendDownloadUseCase.SuspendDownloadCommand(
                null, null, null, null, id, request.reason()));
    }

    private DownloadResponse toResponse(Download aggregate) {
        return new DownloadResponse(
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

    public record CreateDownloadRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDownloadRequest(String title, String description) {}

    public record SuspendDownloadRequest(@NotBlank String reason) {}

    public record DownloadIdResponse(String id) {}

    public record DownloadResponse(
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
