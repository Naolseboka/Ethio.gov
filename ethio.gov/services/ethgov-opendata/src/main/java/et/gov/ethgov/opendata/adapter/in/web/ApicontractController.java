package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.apicontract.ActivateApicontractUseCase;
import et.gov.ethgov.opendata.application.apicontract.CreateApicontractUseCase;
import et.gov.ethgov.opendata.application.apicontract.GetApicontractUseCase;
import et.gov.ethgov.opendata.application.apicontract.ListApicontractUseCase;
import et.gov.ethgov.opendata.application.apicontract.SuspendApicontractUseCase;
import et.gov.ethgov.opendata.application.apicontract.UpdateApicontractUseCase;
import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
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
@RequestMapping("/v1/opendata/apicontract")
public class ApicontractController {
    private final CreateApicontractUseCase createUseCase;
    private final UpdateApicontractUseCase updateUseCase;
    private final ActivateApicontractUseCase activateUseCase;
    private final SuspendApicontractUseCase suspendUseCase;
    private final GetApicontractUseCase getUseCase;
    private final ListApicontractUseCase listUseCase;

    public ApicontractController(
            CreateApicontractUseCase createUseCase,
            UpdateApicontractUseCase updateUseCase,
            ActivateApicontractUseCase activateUseCase,
            SuspendApicontractUseCase suspendUseCase,
            GetApicontractUseCase getUseCase,
            ListApicontractUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApicontractIdResponse create(@Valid @RequestBody CreateApicontractRequest request) {
        var id = createUseCase.execute(new CreateApicontractUseCase.CreateApicontractCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ApicontractIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ApicontractResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ApicontractResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ApicontractIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateApicontractRequest request) {
        var updated = updateUseCase.execute(new UpdateApicontractUseCase.UpdateApicontractCommand(
                null, request.title(), request.description(), null, id, null));
        return new ApicontractIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateApicontractUseCase.ActivateApicontractCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendApicontractRequest request) {
        suspendUseCase.execute(new SuspendApicontractUseCase.SuspendApicontractCommand(
                null, null, null, null, id, request.reason()));
    }

    private ApicontractResponse toResponse(Apicontract aggregate) {
        return new ApicontractResponse(
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

    public record CreateApicontractRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateApicontractRequest(String title, String description) {}

    public record SuspendApicontractRequest(@NotBlank String reason) {}

    public record ApicontractIdResponse(String id) {}

    public record ApicontractResponse(
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
