package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.namechange.ActivateNamechangeUseCase;
import et.gov.ethgov.citizen.application.namechange.CreateNamechangeUseCase;
import et.gov.ethgov.citizen.application.namechange.GetNamechangeUseCase;
import et.gov.ethgov.citizen.application.namechange.ListNamechangeUseCase;
import et.gov.ethgov.citizen.application.namechange.SuspendNamechangeUseCase;
import et.gov.ethgov.citizen.application.namechange.UpdateNamechangeUseCase;
import et.gov.ethgov.citizen.domain.namechange.Namechange;
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
@RequestMapping("/v1/citizen/namechange")
public class NamechangeController {
    private final CreateNamechangeUseCase createUseCase;
    private final UpdateNamechangeUseCase updateUseCase;
    private final ActivateNamechangeUseCase activateUseCase;
    private final SuspendNamechangeUseCase suspendUseCase;
    private final GetNamechangeUseCase getUseCase;
    private final ListNamechangeUseCase listUseCase;

    public NamechangeController(
            CreateNamechangeUseCase createUseCase,
            UpdateNamechangeUseCase updateUseCase,
            ActivateNamechangeUseCase activateUseCase,
            SuspendNamechangeUseCase suspendUseCase,
            GetNamechangeUseCase getUseCase,
            ListNamechangeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NamechangeIdResponse create(@Valid @RequestBody CreateNamechangeRequest request) {
        var id = createUseCase.execute(new CreateNamechangeUseCase.CreateNamechangeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new NamechangeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public NamechangeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<NamechangeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public NamechangeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateNamechangeRequest request) {
        var updated = updateUseCase.execute(new UpdateNamechangeUseCase.UpdateNamechangeCommand(
                null, request.title(), request.description(), null, id, null));
        return new NamechangeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateNamechangeUseCase.ActivateNamechangeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendNamechangeRequest request) {
        suspendUseCase.execute(new SuspendNamechangeUseCase.SuspendNamechangeCommand(
                null, null, null, null, id, request.reason()));
    }

    private NamechangeResponse toResponse(Namechange aggregate) {
        return new NamechangeResponse(
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

    public record CreateNamechangeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateNamechangeRequest(String title, String description) {}

    public record SuspendNamechangeRequest(@NotBlank String reason) {}

    public record NamechangeIdResponse(String id) {}

    public record NamechangeResponse(
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
