package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.stopword.ActivateStopwordUseCase;
import et.gov.ethgov.search.application.stopword.CreateStopwordUseCase;
import et.gov.ethgov.search.application.stopword.GetStopwordUseCase;
import et.gov.ethgov.search.application.stopword.ListStopwordUseCase;
import et.gov.ethgov.search.application.stopword.SuspendStopwordUseCase;
import et.gov.ethgov.search.application.stopword.UpdateStopwordUseCase;
import et.gov.ethgov.search.domain.stopword.Stopword;
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
@RequestMapping("/v1/search/stopword")
public class StopwordController {
    private final CreateStopwordUseCase createUseCase;
    private final UpdateStopwordUseCase updateUseCase;
    private final ActivateStopwordUseCase activateUseCase;
    private final SuspendStopwordUseCase suspendUseCase;
    private final GetStopwordUseCase getUseCase;
    private final ListStopwordUseCase listUseCase;

    public StopwordController(
            CreateStopwordUseCase createUseCase,
            UpdateStopwordUseCase updateUseCase,
            ActivateStopwordUseCase activateUseCase,
            SuspendStopwordUseCase suspendUseCase,
            GetStopwordUseCase getUseCase,
            ListStopwordUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StopwordIdResponse create(@Valid @RequestBody CreateStopwordRequest request) {
        var id = createUseCase.execute(new CreateStopwordUseCase.CreateStopwordCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new StopwordIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public StopwordResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<StopwordResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public StopwordIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateStopwordRequest request) {
        var updated = updateUseCase.execute(new UpdateStopwordUseCase.UpdateStopwordCommand(
                null, request.title(), request.description(), null, id, null));
        return new StopwordIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateStopwordUseCase.ActivateStopwordCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendStopwordRequest request) {
        suspendUseCase.execute(new SuspendStopwordUseCase.SuspendStopwordCommand(
                null, null, null, null, id, request.reason()));
    }

    private StopwordResponse toResponse(Stopword aggregate) {
        return new StopwordResponse(
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

    public record CreateStopwordRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateStopwordRequest(String title, String description) {}

    public record SuspendStopwordRequest(@NotBlank String reason) {}

    public record StopwordIdResponse(String id) {}

    public record StopwordResponse(
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
