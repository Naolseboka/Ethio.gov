package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.supportticket.ActivateSupportticketUseCase;
import et.gov.ethgov.developer.application.supportticket.CreateSupportticketUseCase;
import et.gov.ethgov.developer.application.supportticket.GetSupportticketUseCase;
import et.gov.ethgov.developer.application.supportticket.ListSupportticketUseCase;
import et.gov.ethgov.developer.application.supportticket.SuspendSupportticketUseCase;
import et.gov.ethgov.developer.application.supportticket.UpdateSupportticketUseCase;
import et.gov.ethgov.developer.domain.supportticket.Supportticket;
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
@RequestMapping("/v1/developer/supportticket")
public class SupportticketController {
    private final CreateSupportticketUseCase createUseCase;
    private final UpdateSupportticketUseCase updateUseCase;
    private final ActivateSupportticketUseCase activateUseCase;
    private final SuspendSupportticketUseCase suspendUseCase;
    private final GetSupportticketUseCase getUseCase;
    private final ListSupportticketUseCase listUseCase;

    public SupportticketController(
            CreateSupportticketUseCase createUseCase,
            UpdateSupportticketUseCase updateUseCase,
            ActivateSupportticketUseCase activateUseCase,
            SuspendSupportticketUseCase suspendUseCase,
            GetSupportticketUseCase getUseCase,
            ListSupportticketUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportticketIdResponse create(@Valid @RequestBody CreateSupportticketRequest request) {
        var id = createUseCase.execute(new CreateSupportticketUseCase.CreateSupportticketCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SupportticketIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SupportticketResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SupportticketResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SupportticketIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSupportticketRequest request) {
        var updated = updateUseCase.execute(new UpdateSupportticketUseCase.UpdateSupportticketCommand(
                null, request.title(), request.description(), null, id, null));
        return new SupportticketIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSupportticketUseCase.ActivateSupportticketCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSupportticketRequest request) {
        suspendUseCase.execute(new SuspendSupportticketUseCase.SuspendSupportticketCommand(
                null, null, null, null, id, request.reason()));
    }

    private SupportticketResponse toResponse(Supportticket aggregate) {
        return new SupportticketResponse(
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

    public record CreateSupportticketRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSupportticketRequest(String title, String description) {}

    public record SuspendSupportticketRequest(@NotBlank String reason) {}

    public record SupportticketIdResponse(String id) {}

    public record SupportticketResponse(
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
