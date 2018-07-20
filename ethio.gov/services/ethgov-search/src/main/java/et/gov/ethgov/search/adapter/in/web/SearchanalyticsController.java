package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.searchanalytics.ActivateSearchanalyticsUseCase;
import et.gov.ethgov.search.application.searchanalytics.CreateSearchanalyticsUseCase;
import et.gov.ethgov.search.application.searchanalytics.GetSearchanalyticsUseCase;
import et.gov.ethgov.search.application.searchanalytics.ListSearchanalyticsUseCase;
import et.gov.ethgov.search.application.searchanalytics.SuspendSearchanalyticsUseCase;
import et.gov.ethgov.search.application.searchanalytics.UpdateSearchanalyticsUseCase;
import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
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
@RequestMapping("/v1/search/searchanalytics")
public class SearchanalyticsController {
    private final CreateSearchanalyticsUseCase createUseCase;
    private final UpdateSearchanalyticsUseCase updateUseCase;
    private final ActivateSearchanalyticsUseCase activateUseCase;
    private final SuspendSearchanalyticsUseCase suspendUseCase;
    private final GetSearchanalyticsUseCase getUseCase;
    private final ListSearchanalyticsUseCase listUseCase;

    public SearchanalyticsController(
            CreateSearchanalyticsUseCase createUseCase,
            UpdateSearchanalyticsUseCase updateUseCase,
            ActivateSearchanalyticsUseCase activateUseCase,
            SuspendSearchanalyticsUseCase suspendUseCase,
            GetSearchanalyticsUseCase getUseCase,
            ListSearchanalyticsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SearchanalyticsIdResponse create(@Valid @RequestBody CreateSearchanalyticsRequest request) {
        var id = createUseCase.execute(new CreateSearchanalyticsUseCase.CreateSearchanalyticsCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SearchanalyticsIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SearchanalyticsResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SearchanalyticsResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SearchanalyticsIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSearchanalyticsRequest request) {
        var updated = updateUseCase.execute(new UpdateSearchanalyticsUseCase.UpdateSearchanalyticsCommand(
                null, request.title(), request.description(), null, id, null));
        return new SearchanalyticsIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSearchanalyticsUseCase.ActivateSearchanalyticsCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSearchanalyticsRequest request) {
        suspendUseCase.execute(new SuspendSearchanalyticsUseCase.SuspendSearchanalyticsCommand(
                null, null, null, null, id, request.reason()));
    }

    private SearchanalyticsResponse toResponse(Searchanalytics aggregate) {
        return new SearchanalyticsResponse(
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

    public record CreateSearchanalyticsRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSearchanalyticsRequest(String title, String description) {}

    public record SuspendSearchanalyticsRequest(@NotBlank String reason) {}

    public record SearchanalyticsIdResponse(String id) {}

    public record SearchanalyticsResponse(
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
