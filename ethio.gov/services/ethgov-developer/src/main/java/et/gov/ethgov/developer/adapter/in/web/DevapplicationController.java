package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.devapplication.ActivateDevapplicationUseCase;
import et.gov.ethgov.developer.application.devapplication.CreateDevapplicationUseCase;
import et.gov.ethgov.developer.application.devapplication.GetDevapplicationUseCase;
import et.gov.ethgov.developer.application.devapplication.ListDevapplicationUseCase;
import et.gov.ethgov.developer.application.devapplication.SuspendDevapplicationUseCase;
import et.gov.ethgov.developer.application.devapplication.UpdateDevapplicationUseCase;
import et.gov.ethgov.developer.domain.devapplication.Devapplication;
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
@RequestMapping("/v1/developer/devapplication")
public class DevapplicationController {
    private final CreateDevapplicationUseCase createUseCase;
    private final UpdateDevapplicationUseCase updateUseCase;
    private final ActivateDevapplicationUseCase activateUseCase;
    private final SuspendDevapplicationUseCase suspendUseCase;
    private final GetDevapplicationUseCase getUseCase;
    private final ListDevapplicationUseCase listUseCase;

    public DevapplicationController(
            CreateDevapplicationUseCase createUseCase,
            UpdateDevapplicationUseCase updateUseCase,
            ActivateDevapplicationUseCase activateUseCase,
            SuspendDevapplicationUseCase suspendUseCase,
            GetDevapplicationUseCase getUseCase,
            ListDevapplicationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DevapplicationIdResponse create(@Valid @RequestBody CreateDevapplicationRequest request) {
        var id = createUseCase.execute(new CreateDevapplicationUseCase.CreateDevapplicationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DevapplicationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DevapplicationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DevapplicationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DevapplicationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDevapplicationRequest request) {
        var updated = updateUseCase.execute(new UpdateDevapplicationUseCase.UpdateDevapplicationCommand(
                null, request.title(), request.description(), null, id, null));
        return new DevapplicationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDevapplicationUseCase.ActivateDevapplicationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDevapplicationRequest request) {
        suspendUseCase.execute(new SuspendDevapplicationUseCase.SuspendDevapplicationCommand(
                null, null, null, null, id, request.reason()));
    }

    private DevapplicationResponse toResponse(Devapplication aggregate) {
        return new DevapplicationResponse(
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

    public record CreateDevapplicationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDevapplicationRequest(String title, String description) {}

    public record SuspendDevapplicationRequest(@NotBlank String reason) {}

    public record DevapplicationIdResponse(String id) {}

    public record DevapplicationResponse(
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
