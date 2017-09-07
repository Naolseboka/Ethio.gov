package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.party.ActivatePartyUseCase;
import et.gov.ethgov.case.application.party.CreatePartyUseCase;
import et.gov.ethgov.case.application.party.GetPartyUseCase;
import et.gov.ethgov.case.application.party.ListPartyUseCase;
import et.gov.ethgov.case.application.party.SuspendPartyUseCase;
import et.gov.ethgov.case.application.party.UpdatePartyUseCase;
import et.gov.ethgov.case.domain.party.Party;
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
@RequestMapping("/v1/case/party")
public class PartyController {
    private final CreatePartyUseCase createUseCase;
    private final UpdatePartyUseCase updateUseCase;
    private final ActivatePartyUseCase activateUseCase;
    private final SuspendPartyUseCase suspendUseCase;
    private final GetPartyUseCase getUseCase;
    private final ListPartyUseCase listUseCase;

    public PartyController(
            CreatePartyUseCase createUseCase,
            UpdatePartyUseCase updateUseCase,
            ActivatePartyUseCase activateUseCase,
            SuspendPartyUseCase suspendUseCase,
            GetPartyUseCase getUseCase,
            ListPartyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyIdResponse create(@Valid @RequestBody CreatePartyRequest request) {
        var id = createUseCase.execute(new CreatePartyUseCase.CreatePartyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PartyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PartyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PartyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PartyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePartyRequest request) {
        var updated = updateUseCase.execute(new UpdatePartyUseCase.UpdatePartyCommand(
                null, request.title(), request.description(), null, id, null));
        return new PartyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePartyUseCase.ActivatePartyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPartyRequest request) {
        suspendUseCase.execute(new SuspendPartyUseCase.SuspendPartyCommand(
                null, null, null, null, id, request.reason()));
    }

    private PartyResponse toResponse(Party aggregate) {
        return new PartyResponse(
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

    public record CreatePartyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePartyRequest(String title, String description) {}

    public record SuspendPartyRequest(@NotBlank String reason) {}

    public record PartyIdResponse(String id) {}

    public record PartyResponse(
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
