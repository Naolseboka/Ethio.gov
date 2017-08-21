package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.scorecard.ActivateScorecardUseCase;
import et.gov.ethgov.analytics.application.scorecard.CreateScorecardUseCase;
import et.gov.ethgov.analytics.application.scorecard.GetScorecardUseCase;
import et.gov.ethgov.analytics.application.scorecard.ListScorecardUseCase;
import et.gov.ethgov.analytics.application.scorecard.SuspendScorecardUseCase;
import et.gov.ethgov.analytics.application.scorecard.UpdateScorecardUseCase;
import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
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
@RequestMapping("/v1/analytics/scorecard")
public class ScorecardController {
    private final CreateScorecardUseCase createUseCase;
    private final UpdateScorecardUseCase updateUseCase;
    private final ActivateScorecardUseCase activateUseCase;
    private final SuspendScorecardUseCase suspendUseCase;
    private final GetScorecardUseCase getUseCase;
    private final ListScorecardUseCase listUseCase;

    public ScorecardController(
            CreateScorecardUseCase createUseCase,
            UpdateScorecardUseCase updateUseCase,
            ActivateScorecardUseCase activateUseCase,
            SuspendScorecardUseCase suspendUseCase,
            GetScorecardUseCase getUseCase,
            ListScorecardUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScorecardIdResponse create(@Valid @RequestBody CreateScorecardRequest request) {
        var id = createUseCase.execute(new CreateScorecardUseCase.CreateScorecardCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ScorecardIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ScorecardResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ScorecardResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ScorecardIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateScorecardRequest request) {
        var updated = updateUseCase.execute(new UpdateScorecardUseCase.UpdateScorecardCommand(
                null, request.title(), request.description(), null, id, null));
        return new ScorecardIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateScorecardUseCase.ActivateScorecardCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendScorecardRequest request) {
        suspendUseCase.execute(new SuspendScorecardUseCase.SuspendScorecardCommand(
                null, null, null, null, id, request.reason()));
    }

    private ScorecardResponse toResponse(Scorecard aggregate) {
        return new ScorecardResponse(
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

    public record CreateScorecardRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateScorecardRequest(String title, String description) {}

    public record SuspendScorecardRequest(@NotBlank String reason) {}

    public record ScorecardIdResponse(String id) {}

    public record ScorecardResponse(
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
