package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.addresshistory.ActivateAddresshistoryUseCase;
import et.gov.ethgov.citizen.application.addresshistory.CreateAddresshistoryUseCase;
import et.gov.ethgov.citizen.application.addresshistory.GetAddresshistoryUseCase;
import et.gov.ethgov.citizen.application.addresshistory.ListAddresshistoryUseCase;
import et.gov.ethgov.citizen.application.addresshistory.SuspendAddresshistoryUseCase;
import et.gov.ethgov.citizen.application.addresshistory.UpdateAddresshistoryUseCase;
import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
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
@RequestMapping("/v1/citizen/addresshistory")
public class AddresshistoryController {
    private final CreateAddresshistoryUseCase createUseCase;
    private final UpdateAddresshistoryUseCase updateUseCase;
    private final ActivateAddresshistoryUseCase activateUseCase;
    private final SuspendAddresshistoryUseCase suspendUseCase;
    private final GetAddresshistoryUseCase getUseCase;
    private final ListAddresshistoryUseCase listUseCase;

    public AddresshistoryController(
            CreateAddresshistoryUseCase createUseCase,
            UpdateAddresshistoryUseCase updateUseCase,
            ActivateAddresshistoryUseCase activateUseCase,
            SuspendAddresshistoryUseCase suspendUseCase,
            GetAddresshistoryUseCase getUseCase,
            ListAddresshistoryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddresshistoryIdResponse create(@Valid @RequestBody CreateAddresshistoryRequest request) {
        var id = createUseCase.execute(new CreateAddresshistoryUseCase.CreateAddresshistoryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AddresshistoryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AddresshistoryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AddresshistoryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AddresshistoryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAddresshistoryRequest request) {
        var updated = updateUseCase.execute(new UpdateAddresshistoryUseCase.UpdateAddresshistoryCommand(
                null, request.title(), request.description(), null, id, null));
        return new AddresshistoryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAddresshistoryUseCase.ActivateAddresshistoryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAddresshistoryRequest request) {
        suspendUseCase.execute(new SuspendAddresshistoryUseCase.SuspendAddresshistoryCommand(
                null, null, null, null, id, request.reason()));
    }

    private AddresshistoryResponse toResponse(Addresshistory aggregate) {
        return new AddresshistoryResponse(
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

    public record CreateAddresshistoryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAddresshistoryRequest(String title, String description) {}

    public record SuspendAddresshistoryRequest(@NotBlank String reason) {}

    public record AddresshistoryIdResponse(String id) {}

    public record AddresshistoryResponse(
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
