package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.parcel.ActivateParcelUseCase;
import et.gov.ethgov.land.application.parcel.CreateParcelUseCase;
import et.gov.ethgov.land.application.parcel.GetParcelUseCase;
import et.gov.ethgov.land.application.parcel.ListParcelUseCase;
import et.gov.ethgov.land.application.parcel.SuspendParcelUseCase;
import et.gov.ethgov.land.application.parcel.UpdateParcelUseCase;
import et.gov.ethgov.land.domain.parcel.Parcel;
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
@RequestMapping("/v1/land/parcel")
public class ParcelController {
    private final CreateParcelUseCase createUseCase;
    private final UpdateParcelUseCase updateUseCase;
    private final ActivateParcelUseCase activateUseCase;
    private final SuspendParcelUseCase suspendUseCase;
    private final GetParcelUseCase getUseCase;
    private final ListParcelUseCase listUseCase;

    public ParcelController(
            CreateParcelUseCase createUseCase,
            UpdateParcelUseCase updateUseCase,
            ActivateParcelUseCase activateUseCase,
            SuspendParcelUseCase suspendUseCase,
            GetParcelUseCase getUseCase,
            ListParcelUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParcelIdResponse create(@Valid @RequestBody CreateParcelRequest request) {
        var id = createUseCase.execute(new CreateParcelUseCase.CreateParcelCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ParcelIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ParcelResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ParcelResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ParcelIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateParcelRequest request) {
        var updated = updateUseCase.execute(new UpdateParcelUseCase.UpdateParcelCommand(
                null, request.title(), request.description(), null, id, null));
        return new ParcelIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateParcelUseCase.ActivateParcelCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendParcelRequest request) {
        suspendUseCase.execute(new SuspendParcelUseCase.SuspendParcelCommand(
                null, null, null, null, id, request.reason()));
    }

    private ParcelResponse toResponse(Parcel aggregate) {
        return new ParcelResponse(
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

    public record CreateParcelRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateParcelRequest(String title, String description) {}

    public record SuspendParcelRequest(@NotBlank String reason) {}

    public record ParcelIdResponse(String id) {}

    public record ParcelResponse(
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
