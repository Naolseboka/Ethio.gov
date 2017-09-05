package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.closure.ActivateClosureUseCase;
import et.gov.ethgov.case.application.closure.CreateClosureUseCase;
import et.gov.ethgov.case.application.closure.GetClosureUseCase;
import et.gov.ethgov.case.application.closure.ListClosureUseCase;
import et.gov.ethgov.case.application.closure.SuspendClosureUseCase;
import et.gov.ethgov.case.application.closure.UpdateClosureUseCase;
import et.gov.ethgov.case.domain.closure.Closure;
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
@RequestMapping("/v1/case/closure")
public class ClosureController {
    private final CreateClosureUseCase createUseCase;
    private final UpdateClosureUseCase updateUseCase;
    private final ActivateClosureUseCase activateUseCase;
    private final SuspendClosureUseCase suspendUseCase;
    private final GetClosureUseCase getUseCase;
    private final ListClosureUseCase listUseCase;

    public ClosureController(
            CreateClosureUseCase createUseCase,
            UpdateClosureUseCase updateUseCase,
            ActivateClosureUseCase activateUseCase,
            SuspendClosureUseCase suspendUseCase,
            GetClosureUseCase getUseCase,
            ListClosureUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClosureIdResponse create(@Valid @RequestBody CreateClosureRequest request) {
        var id = createUseCase.execute(new CreateClosureUseCase.CreateClosureCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ClosureIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ClosureResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ClosureResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ClosureIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateClosureRequest request) {
        var updated = updateUseCase.execute(new UpdateClosureUseCase.UpdateClosureCommand(
                null, request.title(), request.description(), null, id, null));
        return new ClosureIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateClosureUseCase.ActivateClosureCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendClosureRequest request) {
        suspendUseCase.execute(new SuspendClosureUseCase.SuspendClosureCommand(
                null, null, null, null, id, request.reason()));
    }

    private ClosureResponse toResponse(Closure aggregate) {
        return new ClosureResponse(
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

    public record CreateClosureRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateClosureRequest(String title, String description) {}

    public record SuspendClosureRequest(@NotBlank String reason) {}

    public record ClosureIdResponse(String id) {}

    public record ClosureResponse(
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
