package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.crs.ActivateCrsUseCase;
import et.gov.ethgov.gis.application.crs.CreateCrsUseCase;
import et.gov.ethgov.gis.application.crs.GetCrsUseCase;
import et.gov.ethgov.gis.application.crs.ListCrsUseCase;
import et.gov.ethgov.gis.application.crs.SuspendCrsUseCase;
import et.gov.ethgov.gis.application.crs.UpdateCrsUseCase;
import et.gov.ethgov.gis.domain.crs.Crs;
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
@RequestMapping("/v1/gis/crs")
public class CrsController {
    private final CreateCrsUseCase createUseCase;
    private final UpdateCrsUseCase updateUseCase;
    private final ActivateCrsUseCase activateUseCase;
    private final SuspendCrsUseCase suspendUseCase;
    private final GetCrsUseCase getUseCase;
    private final ListCrsUseCase listUseCase;

    public CrsController(
            CreateCrsUseCase createUseCase,
            UpdateCrsUseCase updateUseCase,
            ActivateCrsUseCase activateUseCase,
            SuspendCrsUseCase suspendUseCase,
            GetCrsUseCase getUseCase,
            ListCrsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CrsIdResponse create(@Valid @RequestBody CreateCrsRequest request) {
        var id = createUseCase.execute(new CreateCrsUseCase.CreateCrsCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CrsIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CrsResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CrsResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CrsIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCrsRequest request) {
        var updated = updateUseCase.execute(new UpdateCrsUseCase.UpdateCrsCommand(
                null, request.title(), request.description(), null, id, null));
        return new CrsIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCrsUseCase.ActivateCrsCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCrsRequest request) {
        suspendUseCase.execute(new SuspendCrsUseCase.SuspendCrsCommand(
                null, null, null, null, id, request.reason()));
    }

    private CrsResponse toResponse(Crs aggregate) {
        return new CrsResponse(
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

    public record CreateCrsRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCrsRequest(String title, String description) {}

    public record SuspendCrsRequest(@NotBlank String reason) {}

    public record CrsIdResponse(String id) {}

    public record CrsResponse(
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
