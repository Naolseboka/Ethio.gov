package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.datalicense.ActivateDatalicenseUseCase;
import et.gov.ethgov.opendata.application.datalicense.CreateDatalicenseUseCase;
import et.gov.ethgov.opendata.application.datalicense.GetDatalicenseUseCase;
import et.gov.ethgov.opendata.application.datalicense.ListDatalicenseUseCase;
import et.gov.ethgov.opendata.application.datalicense.SuspendDatalicenseUseCase;
import et.gov.ethgov.opendata.application.datalicense.UpdateDatalicenseUseCase;
import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
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
@RequestMapping("/v1/opendata/datalicense")
public class DatalicenseController {
    private final CreateDatalicenseUseCase createUseCase;
    private final UpdateDatalicenseUseCase updateUseCase;
    private final ActivateDatalicenseUseCase activateUseCase;
    private final SuspendDatalicenseUseCase suspendUseCase;
    private final GetDatalicenseUseCase getUseCase;
    private final ListDatalicenseUseCase listUseCase;

    public DatalicenseController(
            CreateDatalicenseUseCase createUseCase,
            UpdateDatalicenseUseCase updateUseCase,
            ActivateDatalicenseUseCase activateUseCase,
            SuspendDatalicenseUseCase suspendUseCase,
            GetDatalicenseUseCase getUseCase,
            ListDatalicenseUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatalicenseIdResponse create(@Valid @RequestBody CreateDatalicenseRequest request) {
        var id = createUseCase.execute(new CreateDatalicenseUseCase.CreateDatalicenseCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DatalicenseIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DatalicenseResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DatalicenseResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DatalicenseIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDatalicenseRequest request) {
        var updated = updateUseCase.execute(new UpdateDatalicenseUseCase.UpdateDatalicenseCommand(
                null, request.title(), request.description(), null, id, null));
        return new DatalicenseIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDatalicenseUseCase.ActivateDatalicenseCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDatalicenseRequest request) {
        suspendUseCase.execute(new SuspendDatalicenseUseCase.SuspendDatalicenseCommand(
                null, null, null, null, id, request.reason()));
    }

    private DatalicenseResponse toResponse(Datalicense aggregate) {
        return new DatalicenseResponse(
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

    public record CreateDatalicenseRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDatalicenseRequest(String title, String description) {}

    public record SuspendDatalicenseRequest(@NotBlank String reason) {}

    public record DatalicenseIdResponse(String id) {}

    public record DatalicenseResponse(
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
