package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.writeoff.ActivateWriteoffUseCase;
import et.gov.ethgov.revenue.application.writeoff.CreateWriteoffUseCase;
import et.gov.ethgov.revenue.application.writeoff.GetWriteoffUseCase;
import et.gov.ethgov.revenue.application.writeoff.ListWriteoffUseCase;
import et.gov.ethgov.revenue.application.writeoff.SuspendWriteoffUseCase;
import et.gov.ethgov.revenue.application.writeoff.UpdateWriteoffUseCase;
import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
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
@RequestMapping("/v1/revenue/writeoff")
public class WriteoffController {
    private final CreateWriteoffUseCase createUseCase;
    private final UpdateWriteoffUseCase updateUseCase;
    private final ActivateWriteoffUseCase activateUseCase;
    private final SuspendWriteoffUseCase suspendUseCase;
    private final GetWriteoffUseCase getUseCase;
    private final ListWriteoffUseCase listUseCase;

    public WriteoffController(
            CreateWriteoffUseCase createUseCase,
            UpdateWriteoffUseCase updateUseCase,
            ActivateWriteoffUseCase activateUseCase,
            SuspendWriteoffUseCase suspendUseCase,
            GetWriteoffUseCase getUseCase,
            ListWriteoffUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriteoffIdResponse create(@Valid @RequestBody CreateWriteoffRequest request) {
        var id = createUseCase.execute(new CreateWriteoffUseCase.CreateWriteoffCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WriteoffIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WriteoffResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WriteoffResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WriteoffIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWriteoffRequest request) {
        var updated = updateUseCase.execute(new UpdateWriteoffUseCase.UpdateWriteoffCommand(
                null, request.title(), request.description(), null, id, null));
        return new WriteoffIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWriteoffUseCase.ActivateWriteoffCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWriteoffRequest request) {
        suspendUseCase.execute(new SuspendWriteoffUseCase.SuspendWriteoffCommand(
                null, null, null, null, id, request.reason()));
    }

    private WriteoffResponse toResponse(Writeoff aggregate) {
        return new WriteoffResponse(
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

    public record CreateWriteoffRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWriteoffRequest(String title, String description) {}

    public record SuspendWriteoffRequest(@NotBlank String reason) {}

    public record WriteoffIdResponse(String id) {}

    public record WriteoffResponse(
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
