package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.refund.ActivateRefundUseCase;
import et.gov.ethgov.payments.application.refund.CreateRefundUseCase;
import et.gov.ethgov.payments.application.refund.GetRefundUseCase;
import et.gov.ethgov.payments.application.refund.ListRefundUseCase;
import et.gov.ethgov.payments.application.refund.SuspendRefundUseCase;
import et.gov.ethgov.payments.application.refund.UpdateRefundUseCase;
import et.gov.ethgov.payments.domain.refund.Refund;
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
@RequestMapping("/v1/payments/refund")
public class RefundController {
    private final CreateRefundUseCase createUseCase;
    private final UpdateRefundUseCase updateUseCase;
    private final ActivateRefundUseCase activateUseCase;
    private final SuspendRefundUseCase suspendUseCase;
    private final GetRefundUseCase getUseCase;
    private final ListRefundUseCase listUseCase;

    public RefundController(
            CreateRefundUseCase createUseCase,
            UpdateRefundUseCase updateUseCase,
            ActivateRefundUseCase activateUseCase,
            SuspendRefundUseCase suspendUseCase,
            GetRefundUseCase getUseCase,
            ListRefundUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RefundIdResponse create(@Valid @RequestBody CreateRefundRequest request) {
        var id = createUseCase.execute(new CreateRefundUseCase.CreateRefundCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RefundIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RefundResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RefundResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RefundIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRefundRequest request) {
        var updated = updateUseCase.execute(new UpdateRefundUseCase.UpdateRefundCommand(
                null, request.title(), request.description(), null, id, null));
        return new RefundIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRefundUseCase.ActivateRefundCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRefundRequest request) {
        suspendUseCase.execute(new SuspendRefundUseCase.SuspendRefundCommand(
                null, null, null, null, id, request.reason()));
    }

    private RefundResponse toResponse(Refund aggregate) {
        return new RefundResponse(
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

    public record CreateRefundRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRefundRequest(String title, String description) {}

    public record SuspendRefundRequest(@NotBlank String reason) {}

    public record RefundIdResponse(String id) {}

    public record RefundResponse(
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
