package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.opendataset.ActivateOpendatasetUseCase;
import et.gov.ethgov.opendata.application.opendataset.CreateOpendatasetUseCase;
import et.gov.ethgov.opendata.application.opendataset.GetOpendatasetUseCase;
import et.gov.ethgov.opendata.application.opendataset.ListOpendatasetUseCase;
import et.gov.ethgov.opendata.application.opendataset.SuspendOpendatasetUseCase;
import et.gov.ethgov.opendata.application.opendataset.UpdateOpendatasetUseCase;
import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
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
@RequestMapping("/v1/opendata/opendataset")
public class OpendatasetController {
    private final CreateOpendatasetUseCase createUseCase;
    private final UpdateOpendatasetUseCase updateUseCase;
    private final ActivateOpendatasetUseCase activateUseCase;
    private final SuspendOpendatasetUseCase suspendUseCase;
    private final GetOpendatasetUseCase getUseCase;
    private final ListOpendatasetUseCase listUseCase;

    public OpendatasetController(
            CreateOpendatasetUseCase createUseCase,
            UpdateOpendatasetUseCase updateUseCase,
            ActivateOpendatasetUseCase activateUseCase,
            SuspendOpendatasetUseCase suspendUseCase,
            GetOpendatasetUseCase getUseCase,
            ListOpendatasetUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpendatasetIdResponse create(@Valid @RequestBody CreateOpendatasetRequest request) {
        var id = createUseCase.execute(new CreateOpendatasetUseCase.CreateOpendatasetCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OpendatasetIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OpendatasetResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OpendatasetResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OpendatasetIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOpendatasetRequest request) {
        var updated = updateUseCase.execute(new UpdateOpendatasetUseCase.UpdateOpendatasetCommand(
                null, request.title(), request.description(), null, id, null));
        return new OpendatasetIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOpendatasetUseCase.ActivateOpendatasetCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOpendatasetRequest request) {
        suspendUseCase.execute(new SuspendOpendatasetUseCase.SuspendOpendatasetCommand(
                null, null, null, null, id, request.reason()));
    }

    private OpendatasetResponse toResponse(Opendataset aggregate) {
        return new OpendatasetResponse(
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

    public record CreateOpendatasetRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOpendatasetRequest(String title, String description) {}

    public record SuspendOpendatasetRequest(@NotBlank String reason) {}

    public record OpendatasetIdResponse(String id) {}

    public record OpendatasetResponse(
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
