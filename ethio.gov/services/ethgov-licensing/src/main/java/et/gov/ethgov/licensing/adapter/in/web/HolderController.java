package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.holder.ActivateHolderUseCase;
import et.gov.ethgov.licensing.application.holder.CreateHolderUseCase;
import et.gov.ethgov.licensing.application.holder.GetHolderUseCase;
import et.gov.ethgov.licensing.application.holder.ListHolderUseCase;
import et.gov.ethgov.licensing.application.holder.SuspendHolderUseCase;
import et.gov.ethgov.licensing.application.holder.UpdateHolderUseCase;
import et.gov.ethgov.licensing.domain.holder.Holder;
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
@RequestMapping("/v1/licensing/holder")
public class HolderController {
    private final CreateHolderUseCase createUseCase;
    private final UpdateHolderUseCase updateUseCase;
    private final ActivateHolderUseCase activateUseCase;
    private final SuspendHolderUseCase suspendUseCase;
    private final GetHolderUseCase getUseCase;
    private final ListHolderUseCase listUseCase;

    public HolderController(
            CreateHolderUseCase createUseCase,
            UpdateHolderUseCase updateUseCase,
            ActivateHolderUseCase activateUseCase,
            SuspendHolderUseCase suspendUseCase,
            GetHolderUseCase getUseCase,
            ListHolderUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HolderIdResponse create(@Valid @RequestBody CreateHolderRequest request) {
        var id = createUseCase.execute(new CreateHolderUseCase.CreateHolderCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HolderIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HolderResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HolderResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HolderIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHolderRequest request) {
        var updated = updateUseCase.execute(new UpdateHolderUseCase.UpdateHolderCommand(
                null, request.title(), request.description(), null, id, null));
        return new HolderIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHolderUseCase.ActivateHolderCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHolderRequest request) {
        suspendUseCase.execute(new SuspendHolderUseCase.SuspendHolderCommand(
                null, null, null, null, id, request.reason()));
    }

    private HolderResponse toResponse(Holder aggregate) {
        return new HolderResponse(
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

    public record CreateHolderRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHolderRequest(String title, String description) {}

    public record SuspendHolderRequest(@NotBlank String reason) {}

    public record HolderIdResponse(String id) {}

    public record HolderResponse(
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
