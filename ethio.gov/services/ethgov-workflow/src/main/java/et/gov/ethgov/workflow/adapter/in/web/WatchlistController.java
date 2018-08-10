package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.watchlist.ActivateWatchlistUseCase;
import et.gov.ethgov.workflow.application.watchlist.CreateWatchlistUseCase;
import et.gov.ethgov.workflow.application.watchlist.GetWatchlistUseCase;
import et.gov.ethgov.workflow.application.watchlist.ListWatchlistUseCase;
import et.gov.ethgov.workflow.application.watchlist.SuspendWatchlistUseCase;
import et.gov.ethgov.workflow.application.watchlist.UpdateWatchlistUseCase;
import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
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
@RequestMapping("/v1/workflow/watchlist")
public class WatchlistController {
    private final CreateWatchlistUseCase createUseCase;
    private final UpdateWatchlistUseCase updateUseCase;
    private final ActivateWatchlistUseCase activateUseCase;
    private final SuspendWatchlistUseCase suspendUseCase;
    private final GetWatchlistUseCase getUseCase;
    private final ListWatchlistUseCase listUseCase;

    public WatchlistController(
            CreateWatchlistUseCase createUseCase,
            UpdateWatchlistUseCase updateUseCase,
            ActivateWatchlistUseCase activateUseCase,
            SuspendWatchlistUseCase suspendUseCase,
            GetWatchlistUseCase getUseCase,
            ListWatchlistUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistIdResponse create(@Valid @RequestBody CreateWatchlistRequest request) {
        var id = createUseCase.execute(new CreateWatchlistUseCase.CreateWatchlistCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WatchlistIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WatchlistResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WatchlistResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WatchlistIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWatchlistRequest request) {
        var updated = updateUseCase.execute(new UpdateWatchlistUseCase.UpdateWatchlistCommand(
                null, request.title(), request.description(), null, id, null));
        return new WatchlistIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWatchlistUseCase.ActivateWatchlistCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWatchlistRequest request) {
        suspendUseCase.execute(new SuspendWatchlistUseCase.SuspendWatchlistCommand(
                null, null, null, null, id, request.reason()));
    }

    private WatchlistResponse toResponse(Watchlist aggregate) {
        return new WatchlistResponse(
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

    public record CreateWatchlistRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWatchlistRequest(String title, String description) {}

    public record SuspendWatchlistRequest(@NotBlank String reason) {}

    public record WatchlistIdResponse(String id) {}

    public record WatchlistResponse(
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
