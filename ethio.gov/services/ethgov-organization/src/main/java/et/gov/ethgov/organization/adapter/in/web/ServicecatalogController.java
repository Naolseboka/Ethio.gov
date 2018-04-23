package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.servicecatalog.ActivateServicecatalogUseCase;
import et.gov.ethgov.organization.application.servicecatalog.CreateServicecatalogUseCase;
import et.gov.ethgov.organization.application.servicecatalog.GetServicecatalogUseCase;
import et.gov.ethgov.organization.application.servicecatalog.ListServicecatalogUseCase;
import et.gov.ethgov.organization.application.servicecatalog.SuspendServicecatalogUseCase;
import et.gov.ethgov.organization.application.servicecatalog.UpdateServicecatalogUseCase;
import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
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
@RequestMapping("/v1/organization/servicecatalog")
public class ServicecatalogController {
    private final CreateServicecatalogUseCase createUseCase;
    private final UpdateServicecatalogUseCase updateUseCase;
    private final ActivateServicecatalogUseCase activateUseCase;
    private final SuspendServicecatalogUseCase suspendUseCase;
    private final GetServicecatalogUseCase getUseCase;
    private final ListServicecatalogUseCase listUseCase;

    public ServicecatalogController(
            CreateServicecatalogUseCase createUseCase,
            UpdateServicecatalogUseCase updateUseCase,
            ActivateServicecatalogUseCase activateUseCase,
            SuspendServicecatalogUseCase suspendUseCase,
            GetServicecatalogUseCase getUseCase,
            ListServicecatalogUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicecatalogIdResponse create(@Valid @RequestBody CreateServicecatalogRequest request) {
        var id = createUseCase.execute(new CreateServicecatalogUseCase.CreateServicecatalogCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ServicecatalogIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ServicecatalogResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ServicecatalogResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ServicecatalogIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateServicecatalogRequest request) {
        var updated = updateUseCase.execute(new UpdateServicecatalogUseCase.UpdateServicecatalogCommand(
                null, request.title(), request.description(), null, id, null));
        return new ServicecatalogIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateServicecatalogUseCase.ActivateServicecatalogCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendServicecatalogRequest request) {
        suspendUseCase.execute(new SuspendServicecatalogUseCase.SuspendServicecatalogCommand(
                null, null, null, null, id, request.reason()));
    }

    private ServicecatalogResponse toResponse(Servicecatalog aggregate) {
        return new ServicecatalogResponse(
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

    public record CreateServicecatalogRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateServicecatalogRequest(String title, String description) {}

    public record SuspendServicecatalogRequest(@NotBlank String reason) {}

    public record ServicecatalogIdResponse(String id) {}

    public record ServicecatalogResponse(
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
