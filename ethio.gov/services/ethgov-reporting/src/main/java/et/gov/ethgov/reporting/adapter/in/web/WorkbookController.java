package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.workbook.ActivateWorkbookUseCase;
import et.gov.ethgov.reporting.application.workbook.CreateWorkbookUseCase;
import et.gov.ethgov.reporting.application.workbook.GetWorkbookUseCase;
import et.gov.ethgov.reporting.application.workbook.ListWorkbookUseCase;
import et.gov.ethgov.reporting.application.workbook.SuspendWorkbookUseCase;
import et.gov.ethgov.reporting.application.workbook.UpdateWorkbookUseCase;
import et.gov.ethgov.reporting.domain.workbook.Workbook;
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
@RequestMapping("/v1/reporting/workbook")
public class WorkbookController {
    private final CreateWorkbookUseCase createUseCase;
    private final UpdateWorkbookUseCase updateUseCase;
    private final ActivateWorkbookUseCase activateUseCase;
    private final SuspendWorkbookUseCase suspendUseCase;
    private final GetWorkbookUseCase getUseCase;
    private final ListWorkbookUseCase listUseCase;

    public WorkbookController(
            CreateWorkbookUseCase createUseCase,
            UpdateWorkbookUseCase updateUseCase,
            ActivateWorkbookUseCase activateUseCase,
            SuspendWorkbookUseCase suspendUseCase,
            GetWorkbookUseCase getUseCase,
            ListWorkbookUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkbookIdResponse create(@Valid @RequestBody CreateWorkbookRequest request) {
        var id = createUseCase.execute(new CreateWorkbookUseCase.CreateWorkbookCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WorkbookIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WorkbookResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WorkbookResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WorkbookIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWorkbookRequest request) {
        var updated = updateUseCase.execute(new UpdateWorkbookUseCase.UpdateWorkbookCommand(
                null, request.title(), request.description(), null, id, null));
        return new WorkbookIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWorkbookUseCase.ActivateWorkbookCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWorkbookRequest request) {
        suspendUseCase.execute(new SuspendWorkbookUseCase.SuspendWorkbookCommand(
                null, null, null, null, id, request.reason()));
    }

    private WorkbookResponse toResponse(Workbook aggregate) {
        return new WorkbookResponse(
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

    public record CreateWorkbookRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWorkbookRequest(String title, String description) {}

    public record SuspendWorkbookRequest(@NotBlank String reason) {}

    public record WorkbookIdResponse(String id) {}

    public record WorkbookResponse(
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
