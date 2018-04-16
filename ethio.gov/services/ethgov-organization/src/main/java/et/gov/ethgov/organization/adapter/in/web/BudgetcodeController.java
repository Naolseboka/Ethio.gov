package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.budgetcode.ActivateBudgetcodeUseCase;
import et.gov.ethgov.organization.application.budgetcode.CreateBudgetcodeUseCase;
import et.gov.ethgov.organization.application.budgetcode.GetBudgetcodeUseCase;
import et.gov.ethgov.organization.application.budgetcode.ListBudgetcodeUseCase;
import et.gov.ethgov.organization.application.budgetcode.SuspendBudgetcodeUseCase;
import et.gov.ethgov.organization.application.budgetcode.UpdateBudgetcodeUseCase;
import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
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
@RequestMapping("/v1/organization/budgetcode")
public class BudgetcodeController {
    private final CreateBudgetcodeUseCase createUseCase;
    private final UpdateBudgetcodeUseCase updateUseCase;
    private final ActivateBudgetcodeUseCase activateUseCase;
    private final SuspendBudgetcodeUseCase suspendUseCase;
    private final GetBudgetcodeUseCase getUseCase;
    private final ListBudgetcodeUseCase listUseCase;

    public BudgetcodeController(
            CreateBudgetcodeUseCase createUseCase,
            UpdateBudgetcodeUseCase updateUseCase,
            ActivateBudgetcodeUseCase activateUseCase,
            SuspendBudgetcodeUseCase suspendUseCase,
            GetBudgetcodeUseCase getUseCase,
            ListBudgetcodeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetcodeIdResponse create(@Valid @RequestBody CreateBudgetcodeRequest request) {
        var id = createUseCase.execute(new CreateBudgetcodeUseCase.CreateBudgetcodeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BudgetcodeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BudgetcodeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BudgetcodeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BudgetcodeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBudgetcodeRequest request) {
        var updated = updateUseCase.execute(new UpdateBudgetcodeUseCase.UpdateBudgetcodeCommand(
                null, request.title(), request.description(), null, id, null));
        return new BudgetcodeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBudgetcodeUseCase.ActivateBudgetcodeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBudgetcodeRequest request) {
        suspendUseCase.execute(new SuspendBudgetcodeUseCase.SuspendBudgetcodeCommand(
                null, null, null, null, id, request.reason()));
    }

    private BudgetcodeResponse toResponse(Budgetcode aggregate) {
        return new BudgetcodeResponse(
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

    public record CreateBudgetcodeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBudgetcodeRequest(String title, String description) {}

    public record SuspendBudgetcodeRequest(@NotBlank String reason) {}

    public record BudgetcodeIdResponse(String id) {}

    public record BudgetcodeResponse(
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
