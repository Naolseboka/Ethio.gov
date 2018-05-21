package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.demolition.ActivateDemolitionUseCase;
import et.gov.ethgov.permits.application.demolition.CreateDemolitionUseCase;
import et.gov.ethgov.permits.application.demolition.GetDemolitionUseCase;
import et.gov.ethgov.permits.application.demolition.ListDemolitionUseCase;
import et.gov.ethgov.permits.application.demolition.SuspendDemolitionUseCase;
import et.gov.ethgov.permits.application.demolition.UpdateDemolitionUseCase;
import et.gov.ethgov.permits.domain.demolition.Demolition;
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
@RequestMapping("/v1/permits/demolition")
public class DemolitionController {
    private final CreateDemolitionUseCase createUseCase;
    private final UpdateDemolitionUseCase updateUseCase;
    private final ActivateDemolitionUseCase activateUseCase;
    private final SuspendDemolitionUseCase suspendUseCase;
    private final GetDemolitionUseCase getUseCase;
    private final ListDemolitionUseCase listUseCase;

    public DemolitionController(
            CreateDemolitionUseCase createUseCase,
            UpdateDemolitionUseCase updateUseCase,
            ActivateDemolitionUseCase activateUseCase,
            SuspendDemolitionUseCase suspendUseCase,
            GetDemolitionUseCase getUseCase,
            ListDemolitionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DemolitionIdResponse create(@Valid @RequestBody CreateDemolitionRequest request) {
        var id = createUseCase.execute(new CreateDemolitionUseCase.CreateDemolitionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DemolitionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DemolitionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DemolitionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DemolitionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDemolitionRequest request) {
        var updated = updateUseCase.execute(new UpdateDemolitionUseCase.UpdateDemolitionCommand(
                null, request.title(), request.description(), null, id, null));
        return new DemolitionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDemolitionUseCase.ActivateDemolitionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDemolitionRequest request) {
        suspendUseCase.execute(new SuspendDemolitionUseCase.SuspendDemolitionCommand(
                null, null, null, null, id, request.reason()));
    }

    private DemolitionResponse toResponse(Demolition aggregate) {
        return new DemolitionResponse(
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

    public record CreateDemolitionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDemolitionRequest(String title, String description) {}

    public record SuspendDemolitionRequest(@NotBlank String reason) {}

    public record DemolitionIdResponse(String id) {}

    public record DemolitionResponse(
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
