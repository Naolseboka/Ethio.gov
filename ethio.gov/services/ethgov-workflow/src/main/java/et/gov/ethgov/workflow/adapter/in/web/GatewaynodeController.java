package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.gatewaynode.ActivateGatewaynodeUseCase;
import et.gov.ethgov.workflow.application.gatewaynode.CreateGatewaynodeUseCase;
import et.gov.ethgov.workflow.application.gatewaynode.GetGatewaynodeUseCase;
import et.gov.ethgov.workflow.application.gatewaynode.ListGatewaynodeUseCase;
import et.gov.ethgov.workflow.application.gatewaynode.SuspendGatewaynodeUseCase;
import et.gov.ethgov.workflow.application.gatewaynode.UpdateGatewaynodeUseCase;
import et.gov.ethgov.workflow.domain.gatewaynode.Gatewaynode;
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
@RequestMapping("/v1/workflow/gatewaynode")
public class GatewaynodeController {
    private final CreateGatewaynodeUseCase createUseCase;
    private final UpdateGatewaynodeUseCase updateUseCase;
    private final ActivateGatewaynodeUseCase activateUseCase;
    private final SuspendGatewaynodeUseCase suspendUseCase;
    private final GetGatewaynodeUseCase getUseCase;
    private final ListGatewaynodeUseCase listUseCase;

    public GatewaynodeController(
            CreateGatewaynodeUseCase createUseCase,
            UpdateGatewaynodeUseCase updateUseCase,
            ActivateGatewaynodeUseCase activateUseCase,
            SuspendGatewaynodeUseCase suspendUseCase,
            GetGatewaynodeUseCase getUseCase,
            ListGatewaynodeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GatewaynodeIdResponse create(@Valid @RequestBody CreateGatewaynodeRequest request) {
        var id = createUseCase.execute(new CreateGatewaynodeUseCase.CreateGatewaynodeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new GatewaynodeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public GatewaynodeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<GatewaynodeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public GatewaynodeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateGatewaynodeRequest request) {
        var updated = updateUseCase.execute(new UpdateGatewaynodeUseCase.UpdateGatewaynodeCommand(
                null, request.title(), request.description(), null, id, null));
        return new GatewaynodeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateGatewaynodeUseCase.ActivateGatewaynodeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendGatewaynodeRequest request) {
        suspendUseCase.execute(new SuspendGatewaynodeUseCase.SuspendGatewaynodeCommand(
                null, null, null, null, id, request.reason()));
    }

    private GatewaynodeResponse toResponse(Gatewaynode aggregate) {
        return new GatewaynodeResponse(
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

    public record CreateGatewaynodeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateGatewaynodeRequest(String title, String description) {}

    public record SuspendGatewaynodeRequest(@NotBlank String reason) {}

    public record GatewaynodeIdResponse(String id) {}

    public record GatewaynodeResponse(
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
