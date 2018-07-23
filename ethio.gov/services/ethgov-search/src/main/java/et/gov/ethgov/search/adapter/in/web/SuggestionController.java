package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.suggestion.ActivateSuggestionUseCase;
import et.gov.ethgov.search.application.suggestion.CreateSuggestionUseCase;
import et.gov.ethgov.search.application.suggestion.GetSuggestionUseCase;
import et.gov.ethgov.search.application.suggestion.ListSuggestionUseCase;
import et.gov.ethgov.search.application.suggestion.SuspendSuggestionUseCase;
import et.gov.ethgov.search.application.suggestion.UpdateSuggestionUseCase;
import et.gov.ethgov.search.domain.suggestion.Suggestion;
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
@RequestMapping("/v1/search/suggestion")
public class SuggestionController {
    private final CreateSuggestionUseCase createUseCase;
    private final UpdateSuggestionUseCase updateUseCase;
    private final ActivateSuggestionUseCase activateUseCase;
    private final SuspendSuggestionUseCase suspendUseCase;
    private final GetSuggestionUseCase getUseCase;
    private final ListSuggestionUseCase listUseCase;

    public SuggestionController(
            CreateSuggestionUseCase createUseCase,
            UpdateSuggestionUseCase updateUseCase,
            ActivateSuggestionUseCase activateUseCase,
            SuspendSuggestionUseCase suspendUseCase,
            GetSuggestionUseCase getUseCase,
            ListSuggestionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuggestionIdResponse create(@Valid @RequestBody CreateSuggestionRequest request) {
        var id = createUseCase.execute(new CreateSuggestionUseCase.CreateSuggestionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SuggestionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SuggestionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SuggestionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SuggestionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSuggestionRequest request) {
        var updated = updateUseCase.execute(new UpdateSuggestionUseCase.UpdateSuggestionCommand(
                null, request.title(), request.description(), null, id, null));
        return new SuggestionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSuggestionUseCase.ActivateSuggestionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSuggestionRequest request) {
        suspendUseCase.execute(new SuspendSuggestionUseCase.SuspendSuggestionCommand(
                null, null, null, null, id, request.reason()));
    }

    private SuggestionResponse toResponse(Suggestion aggregate) {
        return new SuggestionResponse(
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

    public record CreateSuggestionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSuggestionRequest(String title, String description) {}

    public record SuspendSuggestionRequest(@NotBlank String reason) {}

    public record SuggestionIdResponse(String id) {}

    public record SuggestionResponse(
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
