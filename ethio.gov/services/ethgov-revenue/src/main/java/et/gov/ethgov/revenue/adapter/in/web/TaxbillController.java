package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.taxbill.ActivateTaxbillUseCase;
import et.gov.ethgov.revenue.application.taxbill.CreateTaxbillUseCase;
import et.gov.ethgov.revenue.application.taxbill.GetTaxbillUseCase;
import et.gov.ethgov.revenue.application.taxbill.ListTaxbillUseCase;
import et.gov.ethgov.revenue.application.taxbill.SuspendTaxbillUseCase;
import et.gov.ethgov.revenue.application.taxbill.UpdateTaxbillUseCase;
import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
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
@RequestMapping("/v1/revenue/taxbill")
public class TaxbillController {
    private final CreateTaxbillUseCase createUseCase;
    private final UpdateTaxbillUseCase updateUseCase;
    private final ActivateTaxbillUseCase activateUseCase;
    private final SuspendTaxbillUseCase suspendUseCase;
    private final GetTaxbillUseCase getUseCase;
    private final ListTaxbillUseCase listUseCase;

    public TaxbillController(
            CreateTaxbillUseCase createUseCase,
            UpdateTaxbillUseCase updateUseCase,
            ActivateTaxbillUseCase activateUseCase,
            SuspendTaxbillUseCase suspendUseCase,
            GetTaxbillUseCase getUseCase,
            ListTaxbillUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaxbillIdResponse create(@Valid @RequestBody CreateTaxbillRequest request) {
        var id = createUseCase.execute(new CreateTaxbillUseCase.CreateTaxbillCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TaxbillIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TaxbillResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TaxbillResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TaxbillIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTaxbillRequest request) {
        var updated = updateUseCase.execute(new UpdateTaxbillUseCase.UpdateTaxbillCommand(
                null, request.title(), request.description(), null, id, null));
        return new TaxbillIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTaxbillUseCase.ActivateTaxbillCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTaxbillRequest request) {
        suspendUseCase.execute(new SuspendTaxbillUseCase.SuspendTaxbillCommand(
                null, null, null, null, id, request.reason()));
    }

    private TaxbillResponse toResponse(Taxbill aggregate) {
        return new TaxbillResponse(
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

    public record CreateTaxbillRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTaxbillRequest(String title, String description) {}

    public record SuspendTaxbillRequest(@NotBlank String reason) {}

    public record TaxbillIdResponse(String id) {}

    public record TaxbillResponse(
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
