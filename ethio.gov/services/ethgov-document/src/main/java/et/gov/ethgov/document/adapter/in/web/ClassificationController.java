package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.classification.ActivateClassificationUseCase;
import et.gov.ethgov.document.application.classification.CreateClassificationUseCase;
import et.gov.ethgov.document.application.classification.GetClassificationUseCase;
import et.gov.ethgov.document.application.classification.ListClassificationUseCase;
import et.gov.ethgov.document.application.classification.SuspendClassificationUseCase;
import et.gov.ethgov.document.application.classification.UpdateClassificationUseCase;
import et.gov.ethgov.document.domain.classification.Classification;
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
@RequestMapping("/v1/document/classification")
public class ClassificationController {
    private final CreateClassificationUseCase createUseCase;
    private final UpdateClassificationUseCase updateUseCase;
    private final ActivateClassificationUseCase activateUseCase;
    private final SuspendClassificationUseCase suspendUseCase;
    private final GetClassificationUseCase getUseCase;
    private final ListClassificationUseCase listUseCase;

    public ClassificationController(
            CreateClassificationUseCase createUseCase,
            UpdateClassificationUseCase updateUseCase,
            ActivateClassificationUseCase activateUseCase,
            SuspendClassificationUseCase suspendUseCase,
            GetClassificationUseCase getUseCase,
            ListClassificationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassificationIdResponse create(@Valid @RequestBody CreateClassificationRequest request) {
        var id = createUseCase.execute(new CreateClassificationUseCase.CreateClassificationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ClassificationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ClassificationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ClassificationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ClassificationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateClassificationRequest request) {
        var updated = updateUseCase.execute(new UpdateClassificationUseCase.UpdateClassificationCommand(
                null, request.title(), request.description(), null, id, null));
        return new ClassificationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateClassificationUseCase.ActivateClassificationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendClassificationRequest request) {
        suspendUseCase.execute(new SuspendClassificationUseCase.SuspendClassificationCommand(
                null, null, null, null, id, request.reason()));
    }

    private ClassificationResponse toResponse(Classification aggregate) {
        return new ClassificationResponse(
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

    public record CreateClassificationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateClassificationRequest(String title, String description) {}

    public record SuspendClassificationRequest(@NotBlank String reason) {}

    public record ClassificationIdResponse(String id) {}

    public record ClassificationResponse(
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
