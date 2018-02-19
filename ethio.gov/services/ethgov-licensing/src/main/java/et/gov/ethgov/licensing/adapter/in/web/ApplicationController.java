package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.application.ActivateApplicationUseCase;
import et.gov.ethgov.licensing.application.application.CreateApplicationUseCase;
import et.gov.ethgov.licensing.application.application.GetApplicationUseCase;
import et.gov.ethgov.licensing.application.application.ListApplicationUseCase;
import et.gov.ethgov.licensing.application.application.SuspendApplicationUseCase;
import et.gov.ethgov.licensing.application.application.UpdateApplicationUseCase;
import et.gov.ethgov.licensing.domain.application.Application;
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
@RequestMapping("/v1/licensing/application")
public class ApplicationController {
    private final CreateApplicationUseCase createUseCase;
    private final UpdateApplicationUseCase updateUseCase;
    private final ActivateApplicationUseCase activateUseCase;
    private final SuspendApplicationUseCase suspendUseCase;
    private final GetApplicationUseCase getUseCase;
    private final ListApplicationUseCase listUseCase;

    public ApplicationController(
            CreateApplicationUseCase createUseCase,
            UpdateApplicationUseCase updateUseCase,
            ActivateApplicationUseCase activateUseCase,
            SuspendApplicationUseCase suspendUseCase,
            GetApplicationUseCase getUseCase,
            ListApplicationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationIdResponse create(@Valid @RequestBody CreateApplicationRequest request) {
        var id = createUseCase.execute(new CreateApplicationUseCase.CreateApplicationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ApplicationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ApplicationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ApplicationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ApplicationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateApplicationRequest request) {
        var updated = updateUseCase.execute(new UpdateApplicationUseCase.UpdateApplicationCommand(
                null, request.title(), request.description(), null, id, null));
        return new ApplicationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateApplicationUseCase.ActivateApplicationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendApplicationRequest request) {
        suspendUseCase.execute(new SuspendApplicationUseCase.SuspendApplicationCommand(
                null, null, null, null, id, request.reason()));
    }

    private ApplicationResponse toResponse(Application aggregate) {
        return new ApplicationResponse(
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

    public record CreateApplicationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateApplicationRequest(String title, String description) {}

    public record SuspendApplicationRequest(@NotBlank String reason) {}

    public record ApplicationIdResponse(String id) {}

    public record ApplicationResponse(
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
