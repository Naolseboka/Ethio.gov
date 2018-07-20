package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.searchdocument.ActivateSearchdocumentUseCase;
import et.gov.ethgov.search.application.searchdocument.CreateSearchdocumentUseCase;
import et.gov.ethgov.search.application.searchdocument.GetSearchdocumentUseCase;
import et.gov.ethgov.search.application.searchdocument.ListSearchdocumentUseCase;
import et.gov.ethgov.search.application.searchdocument.SuspendSearchdocumentUseCase;
import et.gov.ethgov.search.application.searchdocument.UpdateSearchdocumentUseCase;
import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
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
@RequestMapping("/v1/search/searchdocument")
public class SearchdocumentController {
    private final CreateSearchdocumentUseCase createUseCase;
    private final UpdateSearchdocumentUseCase updateUseCase;
    private final ActivateSearchdocumentUseCase activateUseCase;
    private final SuspendSearchdocumentUseCase suspendUseCase;
    private final GetSearchdocumentUseCase getUseCase;
    private final ListSearchdocumentUseCase listUseCase;

    public SearchdocumentController(
            CreateSearchdocumentUseCase createUseCase,
            UpdateSearchdocumentUseCase updateUseCase,
            ActivateSearchdocumentUseCase activateUseCase,
            SuspendSearchdocumentUseCase suspendUseCase,
            GetSearchdocumentUseCase getUseCase,
            ListSearchdocumentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SearchdocumentIdResponse create(@Valid @RequestBody CreateSearchdocumentRequest request) {
        var id = createUseCase.execute(new CreateSearchdocumentUseCase.CreateSearchdocumentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SearchdocumentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SearchdocumentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SearchdocumentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SearchdocumentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSearchdocumentRequest request) {
        var updated = updateUseCase.execute(new UpdateSearchdocumentUseCase.UpdateSearchdocumentCommand(
                null, request.title(), request.description(), null, id, null));
        return new SearchdocumentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSearchdocumentUseCase.ActivateSearchdocumentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSearchdocumentRequest request) {
        suspendUseCase.execute(new SuspendSearchdocumentUseCase.SuspendSearchdocumentCommand(
                null, null, null, null, id, request.reason()));
    }

    private SearchdocumentResponse toResponse(Searchdocument aggregate) {
        return new SearchdocumentResponse(
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

    public record CreateSearchdocumentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSearchdocumentRequest(String title, String description) {}

    public record SuspendSearchdocumentRequest(@NotBlank String reason) {}

    public record SearchdocumentIdResponse(String id) {}

    public record SearchdocumentResponse(
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
