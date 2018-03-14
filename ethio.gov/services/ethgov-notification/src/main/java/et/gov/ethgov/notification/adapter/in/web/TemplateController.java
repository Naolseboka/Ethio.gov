package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.template.ActivateTemplateUseCase;
import et.gov.ethgov.notification.application.template.CreateTemplateUseCase;
import et.gov.ethgov.notification.application.template.GetTemplateUseCase;
import et.gov.ethgov.notification.application.template.ListTemplateUseCase;
import et.gov.ethgov.notification.application.template.SuspendTemplateUseCase;
import et.gov.ethgov.notification.application.template.UpdateTemplateUseCase;
import et.gov.ethgov.notification.domain.template.Template;
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
@RequestMapping("/v1/notification/template")
public class TemplateController {
    private final CreateTemplateUseCase createUseCase;
    private final UpdateTemplateUseCase updateUseCase;
    private final ActivateTemplateUseCase activateUseCase;
    private final SuspendTemplateUseCase suspendUseCase;
    private final GetTemplateUseCase getUseCase;
    private final ListTemplateUseCase listUseCase;

    public TemplateController(
            CreateTemplateUseCase createUseCase,
            UpdateTemplateUseCase updateUseCase,
            ActivateTemplateUseCase activateUseCase,
            SuspendTemplateUseCase suspendUseCase,
            GetTemplateUseCase getUseCase,
            ListTemplateUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateIdResponse create(@Valid @RequestBody CreateTemplateRequest request) {
        var id = createUseCase.execute(new CreateTemplateUseCase.CreateTemplateCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TemplateIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TemplateResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TemplateResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TemplateIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTemplateRequest request) {
        var updated = updateUseCase.execute(new UpdateTemplateUseCase.UpdateTemplateCommand(
                null, request.title(), request.description(), null, id, null));
        return new TemplateIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTemplateUseCase.ActivateTemplateCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTemplateRequest request) {
        suspendUseCase.execute(new SuspendTemplateUseCase.SuspendTemplateCommand(
                null, null, null, null, id, request.reason()));
    }

    private TemplateResponse toResponse(Template aggregate) {
        return new TemplateResponse(
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

    public record CreateTemplateRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTemplateRequest(String title, String description) {}

    public record SuspendTemplateRequest(@NotBlank String reason) {}

    public record TemplateIdResponse(String id) {}

    public record TemplateResponse(
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
