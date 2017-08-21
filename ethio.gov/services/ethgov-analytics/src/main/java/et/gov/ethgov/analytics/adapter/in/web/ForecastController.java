package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.forecast.ActivateForecastUseCase;
import et.gov.ethgov.analytics.application.forecast.CreateForecastUseCase;
import et.gov.ethgov.analytics.application.forecast.GetForecastUseCase;
import et.gov.ethgov.analytics.application.forecast.ListForecastUseCase;
import et.gov.ethgov.analytics.application.forecast.SuspendForecastUseCase;
import et.gov.ethgov.analytics.application.forecast.UpdateForecastUseCase;
import et.gov.ethgov.analytics.domain.forecast.Forecast;
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
@RequestMapping("/v1/analytics/forecast")
public class ForecastController {
    private final CreateForecastUseCase createUseCase;
    private final UpdateForecastUseCase updateUseCase;
    private final ActivateForecastUseCase activateUseCase;
    private final SuspendForecastUseCase suspendUseCase;
    private final GetForecastUseCase getUseCase;
    private final ListForecastUseCase listUseCase;

    public ForecastController(
            CreateForecastUseCase createUseCase,
            UpdateForecastUseCase updateUseCase,
            ActivateForecastUseCase activateUseCase,
            SuspendForecastUseCase suspendUseCase,
            GetForecastUseCase getUseCase,
            ListForecastUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ForecastIdResponse create(@Valid @RequestBody CreateForecastRequest request) {
        var id = createUseCase.execute(new CreateForecastUseCase.CreateForecastCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ForecastIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ForecastResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ForecastResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ForecastIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateForecastRequest request) {
        var updated = updateUseCase.execute(new UpdateForecastUseCase.UpdateForecastCommand(
                null, request.title(), request.description(), null, id, null));
        return new ForecastIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateForecastUseCase.ActivateForecastCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendForecastRequest request) {
        suspendUseCase.execute(new SuspendForecastUseCase.SuspendForecastCommand(
                null, null, null, null, id, request.reason()));
    }

    private ForecastResponse toResponse(Forecast aggregate) {
        return new ForecastResponse(
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

    public record CreateForecastRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateForecastRequest(String title, String description) {}

    public record SuspendForecastRequest(@NotBlank String reason) {}

    public record ForecastIdResponse(String id) {}

    public record ForecastResponse(
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
