package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.counter.ActivateCounterUseCase;
import et.gov.ethgov.directory.application.counter.CreateCounterUseCase;
import et.gov.ethgov.directory.application.counter.GetCounterUseCase;
import et.gov.ethgov.directory.application.counter.ListCounterUseCase;
import et.gov.ethgov.directory.application.counter.SuspendCounterUseCase;
import et.gov.ethgov.directory.application.counter.UpdateCounterUseCase;
import et.gov.ethgov.directory.domain.counter.Counter;
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
@RequestMapping("/v1/directory/counter")
public class CounterController {
    private final CreateCounterUseCase createUseCase;
    private final UpdateCounterUseCase updateUseCase;
    private final ActivateCounterUseCase activateUseCase;
    private final SuspendCounterUseCase suspendUseCase;
    private final GetCounterUseCase getUseCase;
    private final ListCounterUseCase listUseCase;

    public CounterController(
            CreateCounterUseCase createUseCase,
            UpdateCounterUseCase updateUseCase,
            ActivateCounterUseCase activateUseCase,
            SuspendCounterUseCase suspendUseCase,
            GetCounterUseCase getUseCase,
            ListCounterUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CounterIdResponse create(@Valid @RequestBody CreateCounterRequest request) {
        var id = createUseCase.execute(new CreateCounterUseCase.CreateCounterCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CounterIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CounterResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CounterResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CounterIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCounterRequest request) {
        var updated = updateUseCase.execute(new UpdateCounterUseCase.UpdateCounterCommand(
                null, request.title(), request.description(), null, id, null));
        return new CounterIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCounterUseCase.ActivateCounterCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCounterRequest request) {
        suspendUseCase.execute(new SuspendCounterUseCase.SuspendCounterCommand(
                null, null, null, null, id, request.reason()));
    }

    private CounterResponse toResponse(Counter aggregate) {
        return new CounterResponse(
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

    public record CreateCounterRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCounterRequest(String title, String description) {}

    public record SuspendCounterRequest(@NotBlank String reason) {}

    public record CounterIdResponse(String id) {}

    public record CounterResponse(
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
