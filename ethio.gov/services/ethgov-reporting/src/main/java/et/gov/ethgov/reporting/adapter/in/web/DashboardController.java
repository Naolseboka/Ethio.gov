package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.dashboard.ActivateDashboardUseCase;
import et.gov.ethgov.reporting.application.dashboard.CreateDashboardUseCase;
import et.gov.ethgov.reporting.application.dashboard.GetDashboardUseCase;
import et.gov.ethgov.reporting.application.dashboard.ListDashboardUseCase;
import et.gov.ethgov.reporting.application.dashboard.SuspendDashboardUseCase;
import et.gov.ethgov.reporting.application.dashboard.UpdateDashboardUseCase;
import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
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
@RequestMapping("/v1/reporting/dashboard")
public class DashboardController {
    private final CreateDashboardUseCase createUseCase;
    private final UpdateDashboardUseCase updateUseCase;
    private final ActivateDashboardUseCase activateUseCase;
    private final SuspendDashboardUseCase suspendUseCase;
    private final GetDashboardUseCase getUseCase;
    private final ListDashboardUseCase listUseCase;

    public DashboardController(
            CreateDashboardUseCase createUseCase,
            UpdateDashboardUseCase updateUseCase,
            ActivateDashboardUseCase activateUseCase,
            SuspendDashboardUseCase suspendUseCase,
            GetDashboardUseCase getUseCase,
            ListDashboardUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DashboardIdResponse create(@Valid @RequestBody CreateDashboardRequest request) {
        var id = createUseCase.execute(new CreateDashboardUseCase.CreateDashboardCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DashboardIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DashboardResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DashboardResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DashboardIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDashboardRequest request) {
        var updated = updateUseCase.execute(new UpdateDashboardUseCase.UpdateDashboardCommand(
                null, request.title(), request.description(), null, id, null));
        return new DashboardIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDashboardUseCase.ActivateDashboardCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDashboardRequest request) {
        suspendUseCase.execute(new SuspendDashboardUseCase.SuspendDashboardCommand(
                null, null, null, null, id, request.reason()));
    }

    private DashboardResponse toResponse(Dashboard aggregate) {
        return new DashboardResponse(
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

    public record CreateDashboardRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDashboardRequest(String title, String description) {}

    public record SuspendDashboardRequest(@NotBlank String reason) {}

    public record DashboardIdResponse(String id) {}

    public record DashboardResponse(
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
