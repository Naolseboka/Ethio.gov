package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.appeal.ActivateAppealUseCase;
import et.gov.ethgov.case.application.appeal.CreateAppealUseCase;
import et.gov.ethgov.case.application.appeal.GetAppealUseCase;
import et.gov.ethgov.case.application.appeal.ListAppealUseCase;
import et.gov.ethgov.case.application.appeal.SuspendAppealUseCase;
import et.gov.ethgov.case.application.appeal.UpdateAppealUseCase;
import et.gov.ethgov.case.domain.appeal.Appeal;
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
@RequestMapping("/v1/case/appeal")
public class AppealController {
    private final CreateAppealUseCase createUseCase;
    private final UpdateAppealUseCase updateUseCase;
    private final ActivateAppealUseCase activateUseCase;
    private final SuspendAppealUseCase suspendUseCase;
    private final GetAppealUseCase getUseCase;
    private final ListAppealUseCase listUseCase;

    public AppealController(
            CreateAppealUseCase createUseCase,
            UpdateAppealUseCase updateUseCase,
            ActivateAppealUseCase activateUseCase,
            SuspendAppealUseCase suspendUseCase,
            GetAppealUseCase getUseCase,
            ListAppealUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppealIdResponse create(@Valid @RequestBody CreateAppealRequest request) {
        var id = createUseCase.execute(new CreateAppealUseCase.CreateAppealCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AppealIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AppealResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AppealResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AppealIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAppealRequest request) {
        var updated = updateUseCase.execute(new UpdateAppealUseCase.UpdateAppealCommand(
                null, request.title(), request.description(), null, id, null));
        return new AppealIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAppealUseCase.ActivateAppealCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAppealRequest request) {
        suspendUseCase.execute(new SuspendAppealUseCase.SuspendAppealCommand(
                null, null, null, null, id, request.reason()));
    }

    private AppealResponse toResponse(Appeal aggregate) {
        return new AppealResponse(
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

    public record CreateAppealRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAppealRequest(String title, String description) {}

    public record SuspendAppealRequest(@NotBlank String reason) {}

    public record AppealIdResponse(String id) {}

    public record AppealResponse(
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
