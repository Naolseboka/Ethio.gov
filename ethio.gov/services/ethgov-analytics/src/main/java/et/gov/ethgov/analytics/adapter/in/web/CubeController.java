package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.cube.ActivateCubeUseCase;
import et.gov.ethgov.analytics.application.cube.CreateCubeUseCase;
import et.gov.ethgov.analytics.application.cube.GetCubeUseCase;
import et.gov.ethgov.analytics.application.cube.ListCubeUseCase;
import et.gov.ethgov.analytics.application.cube.SuspendCubeUseCase;
import et.gov.ethgov.analytics.application.cube.UpdateCubeUseCase;
import et.gov.ethgov.analytics.domain.cube.Cube;
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
@RequestMapping("/v1/analytics/cube")
public class CubeController {
    private final CreateCubeUseCase createUseCase;
    private final UpdateCubeUseCase updateUseCase;
    private final ActivateCubeUseCase activateUseCase;
    private final SuspendCubeUseCase suspendUseCase;
    private final GetCubeUseCase getUseCase;
    private final ListCubeUseCase listUseCase;

    public CubeController(
            CreateCubeUseCase createUseCase,
            UpdateCubeUseCase updateUseCase,
            ActivateCubeUseCase activateUseCase,
            SuspendCubeUseCase suspendUseCase,
            GetCubeUseCase getUseCase,
            ListCubeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CubeIdResponse create(@Valid @RequestBody CreateCubeRequest request) {
        var id = createUseCase.execute(new CreateCubeUseCase.CreateCubeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CubeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CubeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CubeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CubeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCubeRequest request) {
        var updated = updateUseCase.execute(new UpdateCubeUseCase.UpdateCubeCommand(
                null, request.title(), request.description(), null, id, null));
        return new CubeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCubeUseCase.ActivateCubeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCubeRequest request) {
        suspendUseCase.execute(new SuspendCubeUseCase.SuspendCubeCommand(
                null, null, null, null, id, request.reason()));
    }

    private CubeResponse toResponse(Cube aggregate) {
        return new CubeResponse(
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

    public record CreateCubeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCubeRequest(String title, String description) {}

    public record SuspendCubeRequest(@NotBlank String reason) {}

    public record CubeIdResponse(String id) {}

    public record CubeResponse(
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
