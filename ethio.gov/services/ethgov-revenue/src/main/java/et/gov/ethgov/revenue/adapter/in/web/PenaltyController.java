package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.penalty.ActivatePenaltyUseCase;
import et.gov.ethgov.revenue.application.penalty.CreatePenaltyUseCase;
import et.gov.ethgov.revenue.application.penalty.GetPenaltyUseCase;
import et.gov.ethgov.revenue.application.penalty.ListPenaltyUseCase;
import et.gov.ethgov.revenue.application.penalty.SuspendPenaltyUseCase;
import et.gov.ethgov.revenue.application.penalty.UpdatePenaltyUseCase;
import et.gov.ethgov.revenue.domain.penalty.Penalty;
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
@RequestMapping("/v1/revenue/penalty")
public class PenaltyController {
    private final CreatePenaltyUseCase createUseCase;
    private final UpdatePenaltyUseCase updateUseCase;
    private final ActivatePenaltyUseCase activateUseCase;
    private final SuspendPenaltyUseCase suspendUseCase;
    private final GetPenaltyUseCase getUseCase;
    private final ListPenaltyUseCase listUseCase;

    public PenaltyController(
            CreatePenaltyUseCase createUseCase,
            UpdatePenaltyUseCase updateUseCase,
            ActivatePenaltyUseCase activateUseCase,
            SuspendPenaltyUseCase suspendUseCase,
            GetPenaltyUseCase getUseCase,
            ListPenaltyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PenaltyIdResponse create(@Valid @RequestBody CreatePenaltyRequest request) {
        var id = createUseCase.execute(new CreatePenaltyUseCase.CreatePenaltyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PenaltyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PenaltyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PenaltyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PenaltyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePenaltyRequest request) {
        var updated = updateUseCase.execute(new UpdatePenaltyUseCase.UpdatePenaltyCommand(
                null, request.title(), request.description(), null, id, null));
        return new PenaltyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePenaltyUseCase.ActivatePenaltyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPenaltyRequest request) {
        suspendUseCase.execute(new SuspendPenaltyUseCase.SuspendPenaltyCommand(
                null, null, null, null, id, request.reason()));
    }

    private PenaltyResponse toResponse(Penalty aggregate) {
        return new PenaltyResponse(
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

    public record CreatePenaltyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePenaltyRequest(String title, String description) {}

    public record SuspendPenaltyRequest(@NotBlank String reason) {}

    public record PenaltyIdResponse(String id) {}

    public record PenaltyResponse(
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
