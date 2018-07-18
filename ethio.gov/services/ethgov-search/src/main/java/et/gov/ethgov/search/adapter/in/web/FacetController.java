package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.facet.ActivateFacetUseCase;
import et.gov.ethgov.search.application.facet.CreateFacetUseCase;
import et.gov.ethgov.search.application.facet.GetFacetUseCase;
import et.gov.ethgov.search.application.facet.ListFacetUseCase;
import et.gov.ethgov.search.application.facet.SuspendFacetUseCase;
import et.gov.ethgov.search.application.facet.UpdateFacetUseCase;
import et.gov.ethgov.search.domain.facet.Facet;
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
@RequestMapping("/v1/search/facet")
public class FacetController {
    private final CreateFacetUseCase createUseCase;
    private final UpdateFacetUseCase updateUseCase;
    private final ActivateFacetUseCase activateUseCase;
    private final SuspendFacetUseCase suspendUseCase;
    private final GetFacetUseCase getUseCase;
    private final ListFacetUseCase listUseCase;

    public FacetController(
            CreateFacetUseCase createUseCase,
            UpdateFacetUseCase updateUseCase,
            ActivateFacetUseCase activateUseCase,
            SuspendFacetUseCase suspendUseCase,
            GetFacetUseCase getUseCase,
            ListFacetUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacetIdResponse create(@Valid @RequestBody CreateFacetRequest request) {
        var id = createUseCase.execute(new CreateFacetUseCase.CreateFacetCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new FacetIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public FacetResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<FacetResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public FacetIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateFacetRequest request) {
        var updated = updateUseCase.execute(new UpdateFacetUseCase.UpdateFacetCommand(
                null, request.title(), request.description(), null, id, null));
        return new FacetIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateFacetUseCase.ActivateFacetCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendFacetRequest request) {
        suspendUseCase.execute(new SuspendFacetUseCase.SuspendFacetCommand(
                null, null, null, null, id, request.reason()));
    }

    private FacetResponse toResponse(Facet aggregate) {
        return new FacetResponse(
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

    public record CreateFacetRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateFacetRequest(String title, String description) {}

    public record SuspendFacetRequest(@NotBlank String reason) {}

    public record FacetIdResponse(String id) {}

    public record FacetResponse(
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
