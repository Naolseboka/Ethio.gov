package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.inspection.ActivateInspectionUseCase;
import et.gov.ethgov.licensing.application.inspection.CreateInspectionUseCase;
import et.gov.ethgov.licensing.application.inspection.GetInspectionUseCase;
import et.gov.ethgov.licensing.application.inspection.ListInspectionUseCase;
import et.gov.ethgov.licensing.application.inspection.SuspendInspectionUseCase;
import et.gov.ethgov.licensing.application.inspection.UpdateInspectionUseCase;
import et.gov.ethgov.licensing.domain.inspection.Inspection;
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
@RequestMapping("/v1/licensing/inspection")
public class InspectionController {
    private final CreateInspectionUseCase createUseCase;
    private final UpdateInspectionUseCase updateUseCase;
    private final ActivateInspectionUseCase activateUseCase;
    private final SuspendInspectionUseCase suspendUseCase;
    private final GetInspectionUseCase getUseCase;
    private final ListInspectionUseCase listUseCase;

    public InspectionController(
            CreateInspectionUseCase createUseCase,
            UpdateInspectionUseCase updateUseCase,
            ActivateInspectionUseCase activateUseCase,
            SuspendInspectionUseCase suspendUseCase,
            GetInspectionUseCase getUseCase,
            ListInspectionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspectionIdResponse create(@Valid @RequestBody CreateInspectionRequest request) {
        var id = createUseCase.execute(new CreateInspectionUseCase.CreateInspectionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new InspectionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public InspectionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<InspectionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public InspectionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateInspectionRequest request) {
        var updated = updateUseCase.execute(new UpdateInspectionUseCase.UpdateInspectionCommand(
                null, request.title(), request.description(), null, id, null));
        return new InspectionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateInspectionUseCase.ActivateInspectionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendInspectionRequest request) {
        suspendUseCase.execute(new SuspendInspectionUseCase.SuspendInspectionCommand(
                null, null, null, null, id, request.reason()));
    }

    private InspectionResponse toResponse(Inspection aggregate) {
        return new InspectionResponse(
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

    public record CreateInspectionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateInspectionRequest(String title, String description) {}

    public record SuspendInspectionRequest(@NotBlank String reason) {}

    public record InspectionIdResponse(String id) {}

    public record InspectionResponse(
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
