package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.riskscore.ActivateRiskscoreUseCase;
import et.gov.ethgov.identity.application.riskscore.CreateRiskscoreUseCase;
import et.gov.ethgov.identity.application.riskscore.GetRiskscoreUseCase;
import et.gov.ethgov.identity.application.riskscore.ListRiskscoreUseCase;
import et.gov.ethgov.identity.application.riskscore.SuspendRiskscoreUseCase;
import et.gov.ethgov.identity.application.riskscore.UpdateRiskscoreUseCase;
import et.gov.ethgov.identity.domain.riskscore.Riskscore;
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
@RequestMapping("/v1/identity/riskscore")
public class RiskscoreController {
    private final CreateRiskscoreUseCase createUseCase;
    private final UpdateRiskscoreUseCase updateUseCase;
    private final ActivateRiskscoreUseCase activateUseCase;
    private final SuspendRiskscoreUseCase suspendUseCase;
    private final GetRiskscoreUseCase getUseCase;
    private final ListRiskscoreUseCase listUseCase;

    public RiskscoreController(
            CreateRiskscoreUseCase createUseCase,
            UpdateRiskscoreUseCase updateUseCase,
            ActivateRiskscoreUseCase activateUseCase,
            SuspendRiskscoreUseCase suspendUseCase,
            GetRiskscoreUseCase getUseCase,
            ListRiskscoreUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiskscoreIdResponse create(@Valid @RequestBody CreateRiskscoreRequest request) {
        var id = createUseCase.execute(new CreateRiskscoreUseCase.CreateRiskscoreCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RiskscoreIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RiskscoreResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RiskscoreResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RiskscoreIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRiskscoreRequest request) {
        var updated = updateUseCase.execute(new UpdateRiskscoreUseCase.UpdateRiskscoreCommand(
                null, request.title(), request.description(), null, id, null));
        return new RiskscoreIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRiskscoreUseCase.ActivateRiskscoreCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRiskscoreRequest request) {
        suspendUseCase.execute(new SuspendRiskscoreUseCase.SuspendRiskscoreCommand(
                null, null, null, null, id, request.reason()));
    }

    private RiskscoreResponse toResponse(Riskscore aggregate) {
        return new RiskscoreResponse(
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

    public record CreateRiskscoreRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRiskscoreRequest(String title, String description) {}

    public record SuspendRiskscoreRequest(@NotBlank String reason) {}

    public record RiskscoreIdResponse(String id) {}

    public record RiskscoreResponse(
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
