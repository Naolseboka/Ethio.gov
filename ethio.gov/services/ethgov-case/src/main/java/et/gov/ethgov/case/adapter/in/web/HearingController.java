package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.hearing.ActivateHearingUseCase;
import et.gov.ethgov.case.application.hearing.CreateHearingUseCase;
import et.gov.ethgov.case.application.hearing.GetHearingUseCase;
import et.gov.ethgov.case.application.hearing.ListHearingUseCase;
import et.gov.ethgov.case.application.hearing.SuspendHearingUseCase;
import et.gov.ethgov.case.application.hearing.UpdateHearingUseCase;
import et.gov.ethgov.case.domain.hearing.Hearing;
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
@RequestMapping("/v1/case/hearing")
public class HearingController {
    private final CreateHearingUseCase createUseCase;
    private final UpdateHearingUseCase updateUseCase;
    private final ActivateHearingUseCase activateUseCase;
    private final SuspendHearingUseCase suspendUseCase;
    private final GetHearingUseCase getUseCase;
    private final ListHearingUseCase listUseCase;

    public HearingController(
            CreateHearingUseCase createUseCase,
            UpdateHearingUseCase updateUseCase,
            ActivateHearingUseCase activateUseCase,
            SuspendHearingUseCase suspendUseCase,
            GetHearingUseCase getUseCase,
            ListHearingUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HearingIdResponse create(@Valid @RequestBody CreateHearingRequest request) {
        var id = createUseCase.execute(new CreateHearingUseCase.CreateHearingCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HearingIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HearingResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HearingResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HearingIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHearingRequest request) {
        var updated = updateUseCase.execute(new UpdateHearingUseCase.UpdateHearingCommand(
                null, request.title(), request.description(), null, id, null));
        return new HearingIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHearingUseCase.ActivateHearingCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHearingRequest request) {
        suspendUseCase.execute(new SuspendHearingUseCase.SuspendHearingCommand(
                null, null, null, null, id, request.reason()));
    }

    private HearingResponse toResponse(Hearing aggregate) {
        return new HearingResponse(
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

    public record CreateHearingRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHearingRequest(String title, String description) {}

    public record SuspendHearingRequest(@NotBlank String reason) {}

    public record HearingIdResponse(String id) {}

    public record HearingResponse(
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
