package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.arrears.ActivateArrearsUseCase;
import et.gov.ethgov.revenue.application.arrears.CreateArrearsUseCase;
import et.gov.ethgov.revenue.application.arrears.GetArrearsUseCase;
import et.gov.ethgov.revenue.application.arrears.ListArrearsUseCase;
import et.gov.ethgov.revenue.application.arrears.SuspendArrearsUseCase;
import et.gov.ethgov.revenue.application.arrears.UpdateArrearsUseCase;
import et.gov.ethgov.revenue.domain.arrears.Arrears;
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
@RequestMapping("/v1/revenue/arrears")
public class ArrearsController {
    private final CreateArrearsUseCase createUseCase;
    private final UpdateArrearsUseCase updateUseCase;
    private final ActivateArrearsUseCase activateUseCase;
    private final SuspendArrearsUseCase suspendUseCase;
    private final GetArrearsUseCase getUseCase;
    private final ListArrearsUseCase listUseCase;

    public ArrearsController(
            CreateArrearsUseCase createUseCase,
            UpdateArrearsUseCase updateUseCase,
            ActivateArrearsUseCase activateUseCase,
            SuspendArrearsUseCase suspendUseCase,
            GetArrearsUseCase getUseCase,
            ListArrearsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArrearsIdResponse create(@Valid @RequestBody CreateArrearsRequest request) {
        var id = createUseCase.execute(new CreateArrearsUseCase.CreateArrearsCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ArrearsIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ArrearsResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ArrearsResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ArrearsIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateArrearsRequest request) {
        var updated = updateUseCase.execute(new UpdateArrearsUseCase.UpdateArrearsCommand(
                null, request.title(), request.description(), null, id, null));
        return new ArrearsIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateArrearsUseCase.ActivateArrearsCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendArrearsRequest request) {
        suspendUseCase.execute(new SuspendArrearsUseCase.SuspendArrearsCommand(
                null, null, null, null, id, request.reason()));
    }

    private ArrearsResponse toResponse(Arrears aggregate) {
        return new ArrearsResponse(
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

    public record CreateArrearsRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateArrearsRequest(String title, String description) {}

    public record SuspendArrearsRequest(@NotBlank String reason) {}

    public record ArrearsIdResponse(String id) {}

    public record ArrearsResponse(
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
