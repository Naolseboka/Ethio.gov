package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.transfer.ActivateTransferUseCase;
import et.gov.ethgov.land.application.transfer.CreateTransferUseCase;
import et.gov.ethgov.land.application.transfer.GetTransferUseCase;
import et.gov.ethgov.land.application.transfer.ListTransferUseCase;
import et.gov.ethgov.land.application.transfer.SuspendTransferUseCase;
import et.gov.ethgov.land.application.transfer.UpdateTransferUseCase;
import et.gov.ethgov.land.domain.transfer.Transfer;
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
@RequestMapping("/v1/land/transfer")
public class TransferController {
    private final CreateTransferUseCase createUseCase;
    private final UpdateTransferUseCase updateUseCase;
    private final ActivateTransferUseCase activateUseCase;
    private final SuspendTransferUseCase suspendUseCase;
    private final GetTransferUseCase getUseCase;
    private final ListTransferUseCase listUseCase;

    public TransferController(
            CreateTransferUseCase createUseCase,
            UpdateTransferUseCase updateUseCase,
            ActivateTransferUseCase activateUseCase,
            SuspendTransferUseCase suspendUseCase,
            GetTransferUseCase getUseCase,
            ListTransferUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferIdResponse create(@Valid @RequestBody CreateTransferRequest request) {
        var id = createUseCase.execute(new CreateTransferUseCase.CreateTransferCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TransferIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TransferResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TransferResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TransferIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTransferRequest request) {
        var updated = updateUseCase.execute(new UpdateTransferUseCase.UpdateTransferCommand(
                null, request.title(), request.description(), null, id, null));
        return new TransferIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTransferUseCase.ActivateTransferCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTransferRequest request) {
        suspendUseCase.execute(new SuspendTransferUseCase.SuspendTransferCommand(
                null, null, null, null, id, request.reason()));
    }

    private TransferResponse toResponse(Transfer aggregate) {
        return new TransferResponse(
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

    public record CreateTransferRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTransferRequest(String title, String description) {}

    public record SuspendTransferRequest(@NotBlank String reason) {}

    public record TransferIdResponse(String id) {}

    public record TransferResponse(
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
