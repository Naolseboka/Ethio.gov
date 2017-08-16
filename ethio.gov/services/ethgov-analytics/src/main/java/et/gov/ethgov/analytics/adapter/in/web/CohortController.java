package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.cohort.ActivateCohortUseCase;
import et.gov.ethgov.analytics.application.cohort.CreateCohortUseCase;
import et.gov.ethgov.analytics.application.cohort.GetCohortUseCase;
import et.gov.ethgov.analytics.application.cohort.ListCohortUseCase;
import et.gov.ethgov.analytics.application.cohort.SuspendCohortUseCase;
import et.gov.ethgov.analytics.application.cohort.UpdateCohortUseCase;
import et.gov.ethgov.analytics.domain.cohort.Cohort;
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
@RequestMapping("/v1/analytics/cohort")
public class CohortController {
    private final CreateCohortUseCase createUseCase;
    private final UpdateCohortUseCase updateUseCase;
    private final ActivateCohortUseCase activateUseCase;
    private final SuspendCohortUseCase suspendUseCase;
    private final GetCohortUseCase getUseCase;
    private final ListCohortUseCase listUseCase;

    public CohortController(
            CreateCohortUseCase createUseCase,
            UpdateCohortUseCase updateUseCase,
            ActivateCohortUseCase activateUseCase,
            SuspendCohortUseCase suspendUseCase,
            GetCohortUseCase getUseCase,
            ListCohortUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CohortIdResponse create(@Valid @RequestBody CreateCohortRequest request) {
        var id = createUseCase.execute(new CreateCohortUseCase.CreateCohortCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CohortIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CohortResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CohortResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CohortIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCohortRequest request) {
        var updated = updateUseCase.execute(new UpdateCohortUseCase.UpdateCohortCommand(
                null, request.title(), request.description(), null, id, null));
        return new CohortIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCohortUseCase.ActivateCohortCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCohortRequest request) {
        suspendUseCase.execute(new SuspendCohortUseCase.SuspendCohortCommand(
                null, null, null, null, id, request.reason()));
    }

    private CohortResponse toResponse(Cohort aggregate) {
        return new CohortResponse(
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

    public record CreateCohortRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCohortRequest(String title, String description) {}

    public record SuspendCohortRequest(@NotBlank String reason) {}

    public record CohortIdResponse(String id) {}

    public record CohortResponse(
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
