package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.sitevisit.ActivateSitevisitUseCase;
import et.gov.ethgov.permits.application.sitevisit.CreateSitevisitUseCase;
import et.gov.ethgov.permits.application.sitevisit.GetSitevisitUseCase;
import et.gov.ethgov.permits.application.sitevisit.ListSitevisitUseCase;
import et.gov.ethgov.permits.application.sitevisit.SuspendSitevisitUseCase;
import et.gov.ethgov.permits.application.sitevisit.UpdateSitevisitUseCase;
import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
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
@RequestMapping("/v1/permits/sitevisit")
public class SitevisitController {
    private final CreateSitevisitUseCase createUseCase;
    private final UpdateSitevisitUseCase updateUseCase;
    private final ActivateSitevisitUseCase activateUseCase;
    private final SuspendSitevisitUseCase suspendUseCase;
    private final GetSitevisitUseCase getUseCase;
    private final ListSitevisitUseCase listUseCase;

    public SitevisitController(
            CreateSitevisitUseCase createUseCase,
            UpdateSitevisitUseCase updateUseCase,
            ActivateSitevisitUseCase activateUseCase,
            SuspendSitevisitUseCase suspendUseCase,
            GetSitevisitUseCase getUseCase,
            ListSitevisitUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SitevisitIdResponse create(@Valid @RequestBody CreateSitevisitRequest request) {
        var id = createUseCase.execute(new CreateSitevisitUseCase.CreateSitevisitCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SitevisitIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SitevisitResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SitevisitResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SitevisitIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSitevisitRequest request) {
        var updated = updateUseCase.execute(new UpdateSitevisitUseCase.UpdateSitevisitCommand(
                null, request.title(), request.description(), null, id, null));
        return new SitevisitIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSitevisitUseCase.ActivateSitevisitCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSitevisitRequest request) {
        suspendUseCase.execute(new SuspendSitevisitUseCase.SuspendSitevisitCommand(
                null, null, null, null, id, request.reason()));
    }

    private SitevisitResponse toResponse(Sitevisit aggregate) {
        return new SitevisitResponse(
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

    public record CreateSitevisitRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSitevisitRequest(String title, String description) {}

    public record SuspendSitevisitRequest(@NotBlank String reason) {}

    public record SitevisitIdResponse(String id) {}

    public record SitevisitResponse(
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
