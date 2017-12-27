package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.feature.ActivateFeatureUseCase;
import et.gov.ethgov.gis.application.feature.CreateFeatureUseCase;
import et.gov.ethgov.gis.application.feature.GetFeatureUseCase;
import et.gov.ethgov.gis.application.feature.ListFeatureUseCase;
import et.gov.ethgov.gis.application.feature.SuspendFeatureUseCase;
import et.gov.ethgov.gis.application.feature.UpdateFeatureUseCase;
import et.gov.ethgov.gis.domain.feature.Feature;
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
@RequestMapping("/v1/gis/feature")
public class FeatureController {
    private final CreateFeatureUseCase createUseCase;
    private final UpdateFeatureUseCase updateUseCase;
    private final ActivateFeatureUseCase activateUseCase;
    private final SuspendFeatureUseCase suspendUseCase;
    private final GetFeatureUseCase getUseCase;
    private final ListFeatureUseCase listUseCase;

    public FeatureController(
            CreateFeatureUseCase createUseCase,
            UpdateFeatureUseCase updateUseCase,
            ActivateFeatureUseCase activateUseCase,
            SuspendFeatureUseCase suspendUseCase,
            GetFeatureUseCase getUseCase,
            ListFeatureUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeatureIdResponse create(@Valid @RequestBody CreateFeatureRequest request) {
        var id = createUseCase.execute(new CreateFeatureUseCase.CreateFeatureCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new FeatureIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public FeatureResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<FeatureResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public FeatureIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateFeatureRequest request) {
        var updated = updateUseCase.execute(new UpdateFeatureUseCase.UpdateFeatureCommand(
                null, request.title(), request.description(), null, id, null));
        return new FeatureIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateFeatureUseCase.ActivateFeatureCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendFeatureRequest request) {
        suspendUseCase.execute(new SuspendFeatureUseCase.SuspendFeatureCommand(
                null, null, null, null, id, request.reason()));
    }

    private FeatureResponse toResponse(Feature aggregate) {
        return new FeatureResponse(
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

    public record CreateFeatureRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateFeatureRequest(String title, String description) {}

    public record SuspendFeatureRequest(@NotBlank String reason) {}

    public record FeatureIdResponse(String id) {}

    public record FeatureResponse(
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
