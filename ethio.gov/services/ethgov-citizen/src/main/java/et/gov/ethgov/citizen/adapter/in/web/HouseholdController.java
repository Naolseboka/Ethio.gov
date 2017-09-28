package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.household.ActivateHouseholdUseCase;
import et.gov.ethgov.citizen.application.household.CreateHouseholdUseCase;
import et.gov.ethgov.citizen.application.household.GetHouseholdUseCase;
import et.gov.ethgov.citizen.application.household.ListHouseholdUseCase;
import et.gov.ethgov.citizen.application.household.SuspendHouseholdUseCase;
import et.gov.ethgov.citizen.application.household.UpdateHouseholdUseCase;
import et.gov.ethgov.citizen.domain.household.Household;
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
@RequestMapping("/v1/citizen/household")
public class HouseholdController {
    private final CreateHouseholdUseCase createUseCase;
    private final UpdateHouseholdUseCase updateUseCase;
    private final ActivateHouseholdUseCase activateUseCase;
    private final SuspendHouseholdUseCase suspendUseCase;
    private final GetHouseholdUseCase getUseCase;
    private final ListHouseholdUseCase listUseCase;

    public HouseholdController(
            CreateHouseholdUseCase createUseCase,
            UpdateHouseholdUseCase updateUseCase,
            ActivateHouseholdUseCase activateUseCase,
            SuspendHouseholdUseCase suspendUseCase,
            GetHouseholdUseCase getUseCase,
            ListHouseholdUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdIdResponse create(@Valid @RequestBody CreateHouseholdRequest request) {
        var id = createUseCase.execute(new CreateHouseholdUseCase.CreateHouseholdCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HouseholdIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HouseholdResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HouseholdResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HouseholdIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHouseholdRequest request) {
        var updated = updateUseCase.execute(new UpdateHouseholdUseCase.UpdateHouseholdCommand(
                null, request.title(), request.description(), null, id, null));
        return new HouseholdIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHouseholdUseCase.ActivateHouseholdCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHouseholdRequest request) {
        suspendUseCase.execute(new SuspendHouseholdUseCase.SuspendHouseholdCommand(
                null, null, null, null, id, request.reason()));
    }

    private HouseholdResponse toResponse(Household aggregate) {
        return new HouseholdResponse(
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

    public record CreateHouseholdRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHouseholdRequest(String title, String description) {}

    public record SuspendHouseholdRequest(@NotBlank String reason) {}

    public record HouseholdIdResponse(String id) {}

    public record HouseholdResponse(
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
