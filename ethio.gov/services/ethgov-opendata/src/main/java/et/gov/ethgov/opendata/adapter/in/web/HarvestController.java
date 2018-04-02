package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.harvest.ActivateHarvestUseCase;
import et.gov.ethgov.opendata.application.harvest.CreateHarvestUseCase;
import et.gov.ethgov.opendata.application.harvest.GetHarvestUseCase;
import et.gov.ethgov.opendata.application.harvest.ListHarvestUseCase;
import et.gov.ethgov.opendata.application.harvest.SuspendHarvestUseCase;
import et.gov.ethgov.opendata.application.harvest.UpdateHarvestUseCase;
import et.gov.ethgov.opendata.domain.harvest.Harvest;
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
@RequestMapping("/v1/opendata/harvest")
public class HarvestController {
    private final CreateHarvestUseCase createUseCase;
    private final UpdateHarvestUseCase updateUseCase;
    private final ActivateHarvestUseCase activateUseCase;
    private final SuspendHarvestUseCase suspendUseCase;
    private final GetHarvestUseCase getUseCase;
    private final ListHarvestUseCase listUseCase;

    public HarvestController(
            CreateHarvestUseCase createUseCase,
            UpdateHarvestUseCase updateUseCase,
            ActivateHarvestUseCase activateUseCase,
            SuspendHarvestUseCase suspendUseCase,
            GetHarvestUseCase getUseCase,
            ListHarvestUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HarvestIdResponse create(@Valid @RequestBody CreateHarvestRequest request) {
        var id = createUseCase.execute(new CreateHarvestUseCase.CreateHarvestCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HarvestIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HarvestResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HarvestResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HarvestIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHarvestRequest request) {
        var updated = updateUseCase.execute(new UpdateHarvestUseCase.UpdateHarvestCommand(
                null, request.title(), request.description(), null, id, null));
        return new HarvestIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHarvestUseCase.ActivateHarvestCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHarvestRequest request) {
        suspendUseCase.execute(new SuspendHarvestUseCase.SuspendHarvestCommand(
                null, null, null, null, id, request.reason()));
    }

    private HarvestResponse toResponse(Harvest aggregate) {
        return new HarvestResponse(
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

    public record CreateHarvestRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHarvestRequest(String title, String description) {}

    public record SuspendHarvestRequest(@NotBlank String reason) {}

    public record HarvestIdResponse(String id) {}

    public record HarvestResponse(
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
