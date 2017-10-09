package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.apiproduct.ActivateApiproductUseCase;
import et.gov.ethgov.developer.application.apiproduct.CreateApiproductUseCase;
import et.gov.ethgov.developer.application.apiproduct.GetApiproductUseCase;
import et.gov.ethgov.developer.application.apiproduct.ListApiproductUseCase;
import et.gov.ethgov.developer.application.apiproduct.SuspendApiproductUseCase;
import et.gov.ethgov.developer.application.apiproduct.UpdateApiproductUseCase;
import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
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
@RequestMapping("/v1/developer/apiproduct")
public class ApiproductController {
    private final CreateApiproductUseCase createUseCase;
    private final UpdateApiproductUseCase updateUseCase;
    private final ActivateApiproductUseCase activateUseCase;
    private final SuspendApiproductUseCase suspendUseCase;
    private final GetApiproductUseCase getUseCase;
    private final ListApiproductUseCase listUseCase;

    public ApiproductController(
            CreateApiproductUseCase createUseCase,
            UpdateApiproductUseCase updateUseCase,
            ActivateApiproductUseCase activateUseCase,
            SuspendApiproductUseCase suspendUseCase,
            GetApiproductUseCase getUseCase,
            ListApiproductUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiproductIdResponse create(@Valid @RequestBody CreateApiproductRequest request) {
        var id = createUseCase.execute(new CreateApiproductUseCase.CreateApiproductCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ApiproductIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ApiproductResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ApiproductResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ApiproductIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateApiproductRequest request) {
        var updated = updateUseCase.execute(new UpdateApiproductUseCase.UpdateApiproductCommand(
                null, request.title(), request.description(), null, id, null));
        return new ApiproductIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateApiproductUseCase.ActivateApiproductCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendApiproductRequest request) {
        suspendUseCase.execute(new SuspendApiproductUseCase.SuspendApiproductCommand(
                null, null, null, null, id, request.reason()));
    }

    private ApiproductResponse toResponse(Apiproduct aggregate) {
        return new ApiproductResponse(
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

    public record CreateApiproductRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateApiproductRequest(String title, String description) {}

    public record SuspendApiproductRequest(@NotBlank String reason) {}

    public record ApiproductIdResponse(String id) {}

    public record ApiproductResponse(
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
