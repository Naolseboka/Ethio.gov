package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.revenueappeal.ActivateRevenueappealUseCase;
import et.gov.ethgov.revenue.application.revenueappeal.CreateRevenueappealUseCase;
import et.gov.ethgov.revenue.application.revenueappeal.GetRevenueappealUseCase;
import et.gov.ethgov.revenue.application.revenueappeal.ListRevenueappealUseCase;
import et.gov.ethgov.revenue.application.revenueappeal.SuspendRevenueappealUseCase;
import et.gov.ethgov.revenue.application.revenueappeal.UpdateRevenueappealUseCase;
import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
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
@RequestMapping("/v1/revenue/revenueappeal")
public class RevenueappealController {
    private final CreateRevenueappealUseCase createUseCase;
    private final UpdateRevenueappealUseCase updateUseCase;
    private final ActivateRevenueappealUseCase activateUseCase;
    private final SuspendRevenueappealUseCase suspendUseCase;
    private final GetRevenueappealUseCase getUseCase;
    private final ListRevenueappealUseCase listUseCase;

    public RevenueappealController(
            CreateRevenueappealUseCase createUseCase,
            UpdateRevenueappealUseCase updateUseCase,
            ActivateRevenueappealUseCase activateUseCase,
            SuspendRevenueappealUseCase suspendUseCase,
            GetRevenueappealUseCase getUseCase,
            ListRevenueappealUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RevenueappealIdResponse create(@Valid @RequestBody CreateRevenueappealRequest request) {
        var id = createUseCase.execute(new CreateRevenueappealUseCase.CreateRevenueappealCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RevenueappealIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RevenueappealResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RevenueappealResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RevenueappealIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRevenueappealRequest request) {
        var updated = updateUseCase.execute(new UpdateRevenueappealUseCase.UpdateRevenueappealCommand(
                null, request.title(), request.description(), null, id, null));
        return new RevenueappealIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRevenueappealUseCase.ActivateRevenueappealCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRevenueappealRequest request) {
        suspendUseCase.execute(new SuspendRevenueappealUseCase.SuspendRevenueappealCommand(
                null, null, null, null, id, request.reason()));
    }

    private RevenueappealResponse toResponse(Revenueappeal aggregate) {
        return new RevenueappealResponse(
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

    public record CreateRevenueappealRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRevenueappealRequest(String title, String description) {}

    public record SuspendRevenueappealRequest(@NotBlank String reason) {}

    public record RevenueappealIdResponse(String id) {}

    public record RevenueappealResponse(
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
