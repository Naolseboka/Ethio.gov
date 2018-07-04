package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.rebate.ActivateRebateUseCase;
import et.gov.ethgov.revenue.application.rebate.CreateRebateUseCase;
import et.gov.ethgov.revenue.application.rebate.GetRebateUseCase;
import et.gov.ethgov.revenue.application.rebate.ListRebateUseCase;
import et.gov.ethgov.revenue.application.rebate.SuspendRebateUseCase;
import et.gov.ethgov.revenue.application.rebate.UpdateRebateUseCase;
import et.gov.ethgov.revenue.domain.rebate.Rebate;
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
@RequestMapping("/v1/revenue/rebate")
public class RebateController {
    private final CreateRebateUseCase createUseCase;
    private final UpdateRebateUseCase updateUseCase;
    private final ActivateRebateUseCase activateUseCase;
    private final SuspendRebateUseCase suspendUseCase;
    private final GetRebateUseCase getUseCase;
    private final ListRebateUseCase listUseCase;

    public RebateController(
            CreateRebateUseCase createUseCase,
            UpdateRebateUseCase updateUseCase,
            ActivateRebateUseCase activateUseCase,
            SuspendRebateUseCase suspendUseCase,
            GetRebateUseCase getUseCase,
            ListRebateUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RebateIdResponse create(@Valid @RequestBody CreateRebateRequest request) {
        var id = createUseCase.execute(new CreateRebateUseCase.CreateRebateCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RebateIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RebateResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RebateResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RebateIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRebateRequest request) {
        var updated = updateUseCase.execute(new UpdateRebateUseCase.UpdateRebateCommand(
                null, request.title(), request.description(), null, id, null));
        return new RebateIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRebateUseCase.ActivateRebateCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRebateRequest request) {
        suspendUseCase.execute(new SuspendRebateUseCase.SuspendRebateCommand(
                null, null, null, null, id, request.reason()));
    }

    private RebateResponse toResponse(Rebate aggregate) {
        return new RebateResponse(
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

    public record CreateRebateRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRebateRequest(String title, String description) {}

    public record SuspendRebateRequest(@NotBlank String reason) {}

    public record RebateIdResponse(String id) {}

    public record RebateResponse(
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
