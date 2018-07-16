package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.collection.ActivateCollectionUseCase;
import et.gov.ethgov.search.application.collection.CreateCollectionUseCase;
import et.gov.ethgov.search.application.collection.GetCollectionUseCase;
import et.gov.ethgov.search.application.collection.ListCollectionUseCase;
import et.gov.ethgov.search.application.collection.SuspendCollectionUseCase;
import et.gov.ethgov.search.application.collection.UpdateCollectionUseCase;
import et.gov.ethgov.search.domain.collection.Collection;
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
@RequestMapping("/v1/search/collection")
public class CollectionController {
    private final CreateCollectionUseCase createUseCase;
    private final UpdateCollectionUseCase updateUseCase;
    private final ActivateCollectionUseCase activateUseCase;
    private final SuspendCollectionUseCase suspendUseCase;
    private final GetCollectionUseCase getUseCase;
    private final ListCollectionUseCase listUseCase;

    public CollectionController(
            CreateCollectionUseCase createUseCase,
            UpdateCollectionUseCase updateUseCase,
            ActivateCollectionUseCase activateUseCase,
            SuspendCollectionUseCase suspendUseCase,
            GetCollectionUseCase getUseCase,
            ListCollectionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionIdResponse create(@Valid @RequestBody CreateCollectionRequest request) {
        var id = createUseCase.execute(new CreateCollectionUseCase.CreateCollectionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CollectionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CollectionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CollectionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CollectionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCollectionRequest request) {
        var updated = updateUseCase.execute(new UpdateCollectionUseCase.UpdateCollectionCommand(
                null, request.title(), request.description(), null, id, null));
        return new CollectionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCollectionUseCase.ActivateCollectionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCollectionRequest request) {
        suspendUseCase.execute(new SuspendCollectionUseCase.SuspendCollectionCommand(
                null, null, null, null, id, request.reason()));
    }

    private CollectionResponse toResponse(Collection aggregate) {
        return new CollectionResponse(
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

    public record CreateCollectionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCollectionRequest(String title, String description) {}

    public record SuspendCollectionRequest(@NotBlank String reason) {}

    public record CollectionIdResponse(String id) {}

    public record CollectionResponse(
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
