package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.cutoff.ActivateCutoffUseCase;
import et.gov.ethgov.payments.application.cutoff.CreateCutoffUseCase;
import et.gov.ethgov.payments.application.cutoff.GetCutoffUseCase;
import et.gov.ethgov.payments.application.cutoff.ListCutoffUseCase;
import et.gov.ethgov.payments.application.cutoff.SuspendCutoffUseCase;
import et.gov.ethgov.payments.application.cutoff.UpdateCutoffUseCase;
import et.gov.ethgov.payments.domain.cutoff.Cutoff;
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
@RequestMapping("/v1/payments/cutoff")
public class CutoffController {
    private final CreateCutoffUseCase createUseCase;
    private final UpdateCutoffUseCase updateUseCase;
    private final ActivateCutoffUseCase activateUseCase;
    private final SuspendCutoffUseCase suspendUseCase;
    private final GetCutoffUseCase getUseCase;
    private final ListCutoffUseCase listUseCase;

    public CutoffController(
            CreateCutoffUseCase createUseCase,
            UpdateCutoffUseCase updateUseCase,
            ActivateCutoffUseCase activateUseCase,
            SuspendCutoffUseCase suspendUseCase,
            GetCutoffUseCase getUseCase,
            ListCutoffUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CutoffIdResponse create(@Valid @RequestBody CreateCutoffRequest request) {
        var id = createUseCase.execute(new CreateCutoffUseCase.CreateCutoffCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CutoffIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CutoffResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CutoffResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CutoffIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCutoffRequest request) {
        var updated = updateUseCase.execute(new UpdateCutoffUseCase.UpdateCutoffCommand(
                null, request.title(), request.description(), null, id, null));
        return new CutoffIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCutoffUseCase.ActivateCutoffCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCutoffRequest request) {
        suspendUseCase.execute(new SuspendCutoffUseCase.SuspendCutoffCommand(
                null, null, null, null, id, request.reason()));
    }

    private CutoffResponse toResponse(Cutoff aggregate) {
        return new CutoffResponse(
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

    public record CreateCutoffRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCutoffRequest(String title, String description) {}

    public record SuspendCutoffRequest(@NotBlank String reason) {}

    public record CutoffIdResponse(String id) {}

    public record CutoffResponse(
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
