package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.fiscalyear.ActivateFiscalyearUseCase;
import et.gov.ethgov.revenue.application.fiscalyear.CreateFiscalyearUseCase;
import et.gov.ethgov.revenue.application.fiscalyear.GetFiscalyearUseCase;
import et.gov.ethgov.revenue.application.fiscalyear.ListFiscalyearUseCase;
import et.gov.ethgov.revenue.application.fiscalyear.SuspendFiscalyearUseCase;
import et.gov.ethgov.revenue.application.fiscalyear.UpdateFiscalyearUseCase;
import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
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
@RequestMapping("/v1/revenue/fiscalyear")
public class FiscalyearController {
    private final CreateFiscalyearUseCase createUseCase;
    private final UpdateFiscalyearUseCase updateUseCase;
    private final ActivateFiscalyearUseCase activateUseCase;
    private final SuspendFiscalyearUseCase suspendUseCase;
    private final GetFiscalyearUseCase getUseCase;
    private final ListFiscalyearUseCase listUseCase;

    public FiscalyearController(
            CreateFiscalyearUseCase createUseCase,
            UpdateFiscalyearUseCase updateUseCase,
            ActivateFiscalyearUseCase activateUseCase,
            SuspendFiscalyearUseCase suspendUseCase,
            GetFiscalyearUseCase getUseCase,
            ListFiscalyearUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FiscalyearIdResponse create(@Valid @RequestBody CreateFiscalyearRequest request) {
        var id = createUseCase.execute(new CreateFiscalyearUseCase.CreateFiscalyearCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new FiscalyearIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public FiscalyearResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<FiscalyearResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public FiscalyearIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateFiscalyearRequest request) {
        var updated = updateUseCase.execute(new UpdateFiscalyearUseCase.UpdateFiscalyearCommand(
                null, request.title(), request.description(), null, id, null));
        return new FiscalyearIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateFiscalyearUseCase.ActivateFiscalyearCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendFiscalyearRequest request) {
        suspendUseCase.execute(new SuspendFiscalyearUseCase.SuspendFiscalyearCommand(
                null, null, null, null, id, request.reason()));
    }

    private FiscalyearResponse toResponse(Fiscalyear aggregate) {
        return new FiscalyearResponse(
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

    public record CreateFiscalyearRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateFiscalyearRequest(String title, String description) {}

    public record SuspendFiscalyearRequest(@NotBlank String reason) {}

    public record FiscalyearIdResponse(String id) {}

    public record FiscalyearResponse(
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
