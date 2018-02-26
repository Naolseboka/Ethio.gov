package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.renewal.ActivateRenewalUseCase;
import et.gov.ethgov.licensing.application.renewal.CreateRenewalUseCase;
import et.gov.ethgov.licensing.application.renewal.GetRenewalUseCase;
import et.gov.ethgov.licensing.application.renewal.ListRenewalUseCase;
import et.gov.ethgov.licensing.application.renewal.SuspendRenewalUseCase;
import et.gov.ethgov.licensing.application.renewal.UpdateRenewalUseCase;
import et.gov.ethgov.licensing.domain.renewal.Renewal;
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
@RequestMapping("/v1/licensing/renewal")
public class RenewalController {
    private final CreateRenewalUseCase createUseCase;
    private final UpdateRenewalUseCase updateUseCase;
    private final ActivateRenewalUseCase activateUseCase;
    private final SuspendRenewalUseCase suspendUseCase;
    private final GetRenewalUseCase getUseCase;
    private final ListRenewalUseCase listUseCase;

    public RenewalController(
            CreateRenewalUseCase createUseCase,
            UpdateRenewalUseCase updateUseCase,
            ActivateRenewalUseCase activateUseCase,
            SuspendRenewalUseCase suspendUseCase,
            GetRenewalUseCase getUseCase,
            ListRenewalUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RenewalIdResponse create(@Valid @RequestBody CreateRenewalRequest request) {
        var id = createUseCase.execute(new CreateRenewalUseCase.CreateRenewalCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RenewalIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RenewalResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RenewalResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RenewalIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRenewalRequest request) {
        var updated = updateUseCase.execute(new UpdateRenewalUseCase.UpdateRenewalCommand(
                null, request.title(), request.description(), null, id, null));
        return new RenewalIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRenewalUseCase.ActivateRenewalCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRenewalRequest request) {
        suspendUseCase.execute(new SuspendRenewalUseCase.SuspendRenewalCommand(
                null, null, null, null, id, request.reason()));
    }

    private RenewalResponse toResponse(Renewal aggregate) {
        return new RenewalResponse(
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

    public record CreateRenewalRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRenewalRequest(String title, String description) {}

    public record SuspendRenewalRequest(@NotBlank String reason) {}

    public record RenewalIdResponse(String id) {}

    public record RenewalResponse(
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
