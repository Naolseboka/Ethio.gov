package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.campaign.ActivateCampaignUseCase;
import et.gov.ethgov.notification.application.campaign.CreateCampaignUseCase;
import et.gov.ethgov.notification.application.campaign.GetCampaignUseCase;
import et.gov.ethgov.notification.application.campaign.ListCampaignUseCase;
import et.gov.ethgov.notification.application.campaign.SuspendCampaignUseCase;
import et.gov.ethgov.notification.application.campaign.UpdateCampaignUseCase;
import et.gov.ethgov.notification.domain.campaign.Campaign;
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
@RequestMapping("/v1/notification/campaign")
public class CampaignController {
    private final CreateCampaignUseCase createUseCase;
    private final UpdateCampaignUseCase updateUseCase;
    private final ActivateCampaignUseCase activateUseCase;
    private final SuspendCampaignUseCase suspendUseCase;
    private final GetCampaignUseCase getUseCase;
    private final ListCampaignUseCase listUseCase;

    public CampaignController(
            CreateCampaignUseCase createUseCase,
            UpdateCampaignUseCase updateUseCase,
            ActivateCampaignUseCase activateUseCase,
            SuspendCampaignUseCase suspendUseCase,
            GetCampaignUseCase getUseCase,
            ListCampaignUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignIdResponse create(@Valid @RequestBody CreateCampaignRequest request) {
        var id = createUseCase.execute(new CreateCampaignUseCase.CreateCampaignCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CampaignIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CampaignResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CampaignResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CampaignIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCampaignRequest request) {
        var updated = updateUseCase.execute(new UpdateCampaignUseCase.UpdateCampaignCommand(
                null, request.title(), request.description(), null, id, null));
        return new CampaignIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCampaignUseCase.ActivateCampaignCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCampaignRequest request) {
        suspendUseCase.execute(new SuspendCampaignUseCase.SuspendCampaignCommand(
                null, null, null, null, id, request.reason()));
    }

    private CampaignResponse toResponse(Campaign aggregate) {
        return new CampaignResponse(
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

    public record CreateCampaignRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCampaignRequest(String title, String description) {}

    public record SuspendCampaignRequest(@NotBlank String reason) {}

    public record CampaignIdResponse(String id) {}

    public record CampaignResponse(
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
