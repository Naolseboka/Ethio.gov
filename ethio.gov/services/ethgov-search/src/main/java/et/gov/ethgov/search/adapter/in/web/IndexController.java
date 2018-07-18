package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.index.ActivateIndexUseCase;
import et.gov.ethgov.search.application.index.CreateIndexUseCase;
import et.gov.ethgov.search.application.index.GetIndexUseCase;
import et.gov.ethgov.search.application.index.ListIndexUseCase;
import et.gov.ethgov.search.application.index.SuspendIndexUseCase;
import et.gov.ethgov.search.application.index.UpdateIndexUseCase;
import et.gov.ethgov.search.domain.index.Index;
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
@RequestMapping("/v1/search/index")
public class IndexController {
    private final CreateIndexUseCase createUseCase;
    private final UpdateIndexUseCase updateUseCase;
    private final ActivateIndexUseCase activateUseCase;
    private final SuspendIndexUseCase suspendUseCase;
    private final GetIndexUseCase getUseCase;
    private final ListIndexUseCase listUseCase;

    public IndexController(
            CreateIndexUseCase createUseCase,
            UpdateIndexUseCase updateUseCase,
            ActivateIndexUseCase activateUseCase,
            SuspendIndexUseCase suspendUseCase,
            GetIndexUseCase getUseCase,
            ListIndexUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IndexIdResponse create(@Valid @RequestBody CreateIndexRequest request) {
        var id = createUseCase.execute(new CreateIndexUseCase.CreateIndexCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new IndexIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public IndexResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<IndexResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public IndexIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateIndexRequest request) {
        var updated = updateUseCase.execute(new UpdateIndexUseCase.UpdateIndexCommand(
                null, request.title(), request.description(), null, id, null));
        return new IndexIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateIndexUseCase.ActivateIndexCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendIndexRequest request) {
        suspendUseCase.execute(new SuspendIndexUseCase.SuspendIndexCommand(
                null, null, null, null, id, request.reason()));
    }

    private IndexResponse toResponse(Index aggregate) {
        return new IndexResponse(
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

    public record CreateIndexRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateIndexRequest(String title, String description) {}

    public record SuspendIndexRequest(@NotBlank String reason) {}

    public record IndexIdResponse(String id) {}

    public record IndexResponse(
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
