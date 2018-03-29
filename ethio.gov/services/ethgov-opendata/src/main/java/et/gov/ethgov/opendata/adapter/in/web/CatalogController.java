package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.catalog.ActivateCatalogUseCase;
import et.gov.ethgov.opendata.application.catalog.CreateCatalogUseCase;
import et.gov.ethgov.opendata.application.catalog.GetCatalogUseCase;
import et.gov.ethgov.opendata.application.catalog.ListCatalogUseCase;
import et.gov.ethgov.opendata.application.catalog.SuspendCatalogUseCase;
import et.gov.ethgov.opendata.application.catalog.UpdateCatalogUseCase;
import et.gov.ethgov.opendata.domain.catalog.Catalog;
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
@RequestMapping("/v1/opendata/catalog")
public class CatalogController {
    private final CreateCatalogUseCase createUseCase;
    private final UpdateCatalogUseCase updateUseCase;
    private final ActivateCatalogUseCase activateUseCase;
    private final SuspendCatalogUseCase suspendUseCase;
    private final GetCatalogUseCase getUseCase;
    private final ListCatalogUseCase listUseCase;

    public CatalogController(
            CreateCatalogUseCase createUseCase,
            UpdateCatalogUseCase updateUseCase,
            ActivateCatalogUseCase activateUseCase,
            SuspendCatalogUseCase suspendUseCase,
            GetCatalogUseCase getUseCase,
            ListCatalogUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogIdResponse create(@Valid @RequestBody CreateCatalogRequest request) {
        var id = createUseCase.execute(new CreateCatalogUseCase.CreateCatalogCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CatalogIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CatalogResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CatalogResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CatalogIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCatalogRequest request) {
        var updated = updateUseCase.execute(new UpdateCatalogUseCase.UpdateCatalogCommand(
                null, request.title(), request.description(), null, id, null));
        return new CatalogIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCatalogUseCase.ActivateCatalogCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCatalogRequest request) {
        suspendUseCase.execute(new SuspendCatalogUseCase.SuspendCatalogCommand(
                null, null, null, null, id, request.reason()));
    }

    private CatalogResponse toResponse(Catalog aggregate) {
        return new CatalogResponse(
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

    public record CreateCatalogRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCatalogRequest(String title, String description) {}

    public record SuspendCatalogRequest(@NotBlank String reason) {}

    public record CatalogIdResponse(String id) {}

    public record CatalogResponse(
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
