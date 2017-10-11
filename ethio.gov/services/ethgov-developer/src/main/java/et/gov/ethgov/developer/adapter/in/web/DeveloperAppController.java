package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.app.ActivateDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.CancelDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.GetDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.RegisterDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.RetireDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.SuspendDeveloperAppUseCase;
import et.gov.ethgov.developer.application.app.UpdateDeveloperAppUseCase;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/apps")
public class DeveloperAppController {

    private final RegisterDeveloperAppUseCase registerApp;
    private final UpdateDeveloperAppUseCase updateApp;
    private final ActivateDeveloperAppUseCase activateApp;
    private final SuspendDeveloperAppUseCase suspendApp;
    private final RetireDeveloperAppUseCase retireApp;
    private final CancelDeveloperAppUseCase cancelApp;
    private final GetDeveloperAppUseCase getApp;

    public DeveloperAppController(
            RegisterDeveloperAppUseCase registerApp,
            UpdateDeveloperAppUseCase updateApp,
            ActivateDeveloperAppUseCase activateApp,
            SuspendDeveloperAppUseCase suspendApp,
            RetireDeveloperAppUseCase retireApp,
            CancelDeveloperAppUseCase cancelApp,
            GetDeveloperAppUseCase getApp) {
        this.registerApp = registerApp;
        this.updateApp = updateApp;
        this.activateApp = activateApp;
        this.suspendApp = suspendApp;
        this.retireApp = retireApp;
        this.cancelApp = cancelApp;
        this.getApp = getApp;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppIdResponse register(@Valid @RequestBody RegisterRequest request) {
        return new AppIdResponse(registerApp
                .execute(new RegisterDeveloperAppUseCase.Command(
                        request.tenantId(),
                        request.slug(),
                        request.name(),
                        request.description(),
                        request.ownerOrgId(),
                        request.contactEmail(),
                        request.allowedScopes()))
                .value());
    }

    @PutMapping("/{developerAppId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String developerAppId, @Valid @RequestBody UpdateRequest request) {
        updateApp.execute(new UpdateDeveloperAppUseCase.Command(
                developerAppId,
                request.name(),
                request.description(),
                request.ownerOrgId(),
                request.contactEmail(),
                request.allowedScopes()));
    }

    @GetMapping("/lookup")
    public AppResponse lookup(
            @RequestParam String tenantId,
            @RequestParam(required = false) String appNumber,
            @RequestParam(required = false) String slug) {
        if (slug != null && !slug.isBlank()) {
            return AppResponse.from(getApp.bySlug(tenantId, slug));
        }
        return AppResponse.from(getApp.byNumber(tenantId, appNumber));
    }

    @GetMapping("/{developerAppId}")
    public AppResponse get(@PathVariable String developerAppId) {
        return AppResponse.from(getApp.byId(developerAppId));
    }

    @PostMapping("/{developerAppId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable String developerAppId) {
        activateApp.execute(developerAppId);
    }

    @PostMapping("/{developerAppId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(
            @PathVariable String developerAppId, @Valid @RequestBody ReasonRequest request) {
        suspendApp.execute(developerAppId, request.reason());
    }

    @PostMapping("/{developerAppId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String developerAppId) {
        retireApp.execute(developerAppId);
    }

    @PostMapping("/{developerAppId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable String developerAppId, @Valid @RequestBody ReasonRequest request) {
        cancelApp.execute(developerAppId, request.reason());
    }

    public record RegisterRequest(
            @NotBlank String tenantId,
            @NotBlank String slug,
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            String ownerOrgId,
            @NotBlank String contactEmail,
            @NotBlank String allowedScopes) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            String ownerOrgId,
            @NotBlank String contactEmail,
            @NotBlank String allowedScopes) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record AppIdResponse(String developerAppId) {}

    public record AppResponse(
            String developerAppId,
            String tenantId,
            String appNumber,
            String slug,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            String allowedScopes,
            String status) {

        static AppResponse from(DeveloperApp app) {
            return new AppResponse(
                    app.id().value(),
                    app.tenantId().value(),
                    app.appNumber().value(),
                    app.slug().value(),
                    app.name(),
                    app.description().orElse(null),
                    app.ownerOrgId().orElse(null),
                    app.contactEmail(),
                    app.allowedScopes().json(),
                    app.status().name());
        }
    }
}
