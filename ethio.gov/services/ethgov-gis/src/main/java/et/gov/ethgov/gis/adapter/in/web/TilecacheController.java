package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.tilecache.ActivateTilecacheUseCase;
import et.gov.ethgov.gis.application.tilecache.CreateTilecacheUseCase;
import et.gov.ethgov.gis.application.tilecache.GetTilecacheUseCase;
import et.gov.ethgov.gis.application.tilecache.ListTilecacheUseCase;
import et.gov.ethgov.gis.application.tilecache.SuspendTilecacheUseCase;
import et.gov.ethgov.gis.application.tilecache.UpdateTilecacheUseCase;
import et.gov.ethgov.gis.domain.tilecache.Tilecache;
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
@RequestMapping("/v1/gis/tilecache")
public class TilecacheController {
    private final CreateTilecacheUseCase createUseCase;
    private final UpdateTilecacheUseCase updateUseCase;
    private final ActivateTilecacheUseCase activateUseCase;
    private final SuspendTilecacheUseCase suspendUseCase;
    private final GetTilecacheUseCase getUseCase;
    private final ListTilecacheUseCase listUseCase;

    public TilecacheController(
            CreateTilecacheUseCase createUseCase,
            UpdateTilecacheUseCase updateUseCase,
            ActivateTilecacheUseCase activateUseCase,
            SuspendTilecacheUseCase suspendUseCase,
            GetTilecacheUseCase getUseCase,
            ListTilecacheUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TilecacheIdResponse create(@Valid @RequestBody CreateTilecacheRequest request) {
        var id = createUseCase.execute(new CreateTilecacheUseCase.CreateTilecacheCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TilecacheIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TilecacheResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TilecacheResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TilecacheIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTilecacheRequest request) {
        var updated = updateUseCase.execute(new UpdateTilecacheUseCase.UpdateTilecacheCommand(
                null, request.title(), request.description(), null, id, null));
        return new TilecacheIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTilecacheUseCase.ActivateTilecacheCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTilecacheRequest request) {
        suspendUseCase.execute(new SuspendTilecacheUseCase.SuspendTilecacheCommand(
                null, null, null, null, id, request.reason()));
    }

    private TilecacheResponse toResponse(Tilecache aggregate) {
        return new TilecacheResponse(
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

    public record CreateTilecacheRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTilecacheRequest(String title, String description) {}

    public record SuspendTilecacheRequest(@NotBlank String reason) {}

    public record TilecacheIdResponse(String id) {}

    public record TilecacheResponse(
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
