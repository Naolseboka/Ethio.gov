package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.servicedesk.ActivateServicedeskUseCase;
import et.gov.ethgov.directory.application.servicedesk.CreateServicedeskUseCase;
import et.gov.ethgov.directory.application.servicedesk.GetServicedeskUseCase;
import et.gov.ethgov.directory.application.servicedesk.ListServicedeskUseCase;
import et.gov.ethgov.directory.application.servicedesk.SuspendServicedeskUseCase;
import et.gov.ethgov.directory.application.servicedesk.UpdateServicedeskUseCase;
import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
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
@RequestMapping("/v1/directory/servicedesk")
public class ServicedeskController {
    private final CreateServicedeskUseCase createUseCase;
    private final UpdateServicedeskUseCase updateUseCase;
    private final ActivateServicedeskUseCase activateUseCase;
    private final SuspendServicedeskUseCase suspendUseCase;
    private final GetServicedeskUseCase getUseCase;
    private final ListServicedeskUseCase listUseCase;

    public ServicedeskController(
            CreateServicedeskUseCase createUseCase,
            UpdateServicedeskUseCase updateUseCase,
            ActivateServicedeskUseCase activateUseCase,
            SuspendServicedeskUseCase suspendUseCase,
            GetServicedeskUseCase getUseCase,
            ListServicedeskUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicedeskIdResponse create(@Valid @RequestBody CreateServicedeskRequest request) {
        var id = createUseCase.execute(new CreateServicedeskUseCase.CreateServicedeskCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ServicedeskIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ServicedeskResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ServicedeskResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ServicedeskIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateServicedeskRequest request) {
        var updated = updateUseCase.execute(new UpdateServicedeskUseCase.UpdateServicedeskCommand(
                null, request.title(), request.description(), null, id, null));
        return new ServicedeskIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateServicedeskUseCase.ActivateServicedeskCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendServicedeskRequest request) {
        suspendUseCase.execute(new SuspendServicedeskUseCase.SuspendServicedeskCommand(
                null, null, null, null, id, request.reason()));
    }

    private ServicedeskResponse toResponse(Servicedesk aggregate) {
        return new ServicedeskResponse(
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

    public record CreateServicedeskRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateServicedeskRequest(String title, String description) {}

    public record SuspendServicedeskRequest(@NotBlank String reason) {}

    public record ServicedeskIdResponse(String id) {}

    public record ServicedeskResponse(
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
