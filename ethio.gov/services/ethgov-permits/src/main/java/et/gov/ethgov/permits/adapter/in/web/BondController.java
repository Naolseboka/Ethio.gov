package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.bond.ActivateBondUseCase;
import et.gov.ethgov.permits.application.bond.CreateBondUseCase;
import et.gov.ethgov.permits.application.bond.GetBondUseCase;
import et.gov.ethgov.permits.application.bond.ListBondUseCase;
import et.gov.ethgov.permits.application.bond.SuspendBondUseCase;
import et.gov.ethgov.permits.application.bond.UpdateBondUseCase;
import et.gov.ethgov.permits.domain.bond.Bond;
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
@RequestMapping("/v1/permits/bond")
public class BondController {
    private final CreateBondUseCase createUseCase;
    private final UpdateBondUseCase updateUseCase;
    private final ActivateBondUseCase activateUseCase;
    private final SuspendBondUseCase suspendUseCase;
    private final GetBondUseCase getUseCase;
    private final ListBondUseCase listUseCase;

    public BondController(
            CreateBondUseCase createUseCase,
            UpdateBondUseCase updateUseCase,
            ActivateBondUseCase activateUseCase,
            SuspendBondUseCase suspendUseCase,
            GetBondUseCase getUseCase,
            ListBondUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BondIdResponse create(@Valid @RequestBody CreateBondRequest request) {
        var id = createUseCase.execute(new CreateBondUseCase.CreateBondCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BondIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BondResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BondResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BondIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBondRequest request) {
        var updated = updateUseCase.execute(new UpdateBondUseCase.UpdateBondCommand(
                null, request.title(), request.description(), null, id, null));
        return new BondIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBondUseCase.ActivateBondCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBondRequest request) {
        suspendUseCase.execute(new SuspendBondUseCase.SuspendBondCommand(
                null, null, null, null, id, request.reason()));
    }

    private BondResponse toResponse(Bond aggregate) {
        return new BondResponse(
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

    public record CreateBondRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBondRequest(String title, String description) {}

    public record SuspendBondRequest(@NotBlank String reason) {}

    public record BondIdResponse(String id) {}

    public record BondResponse(
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
