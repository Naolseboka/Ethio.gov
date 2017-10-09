package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.credential.GetApiCredentialUseCase;
import et.gov.ethgov.developer.application.credential.IssueApiCredentialUseCase;
import et.gov.ethgov.developer.application.credential.RevokeApiCredentialUseCase;
import et.gov.ethgov.developer.domain.credential.ApiCredential;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/credentials")
public class ApiCredentialController {

    private final IssueApiCredentialUseCase issueCredential;
    private final RevokeApiCredentialUseCase revokeCredential;
    private final GetApiCredentialUseCase getCredential;

    public ApiCredentialController(
            IssueApiCredentialUseCase issueCredential,
            RevokeApiCredentialUseCase revokeCredential,
            GetApiCredentialUseCase getCredential) {
        this.issueCredential = issueCredential;
        this.revokeCredential = revokeCredential;
        this.getCredential = getCredential;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssuedCredentialResponse issue(@Valid @RequestBody IssueRequest request) {
        IssueApiCredentialUseCase.Result result = issueCredential.execute(
                new IssueApiCredentialUseCase.Command(
                        request.tenantId(),
                        request.developerAppId(),
                        request.label(),
                        request.expiresAt()));
        return new IssuedCredentialResponse(result.apiCredentialId(), result.apiKey(), result.keyPrefix());
    }

    @PostMapping("/{apiCredentialId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(
            @PathVariable String apiCredentialId, @Valid @RequestBody ReasonRequest request) {
        revokeCredential.execute(apiCredentialId, request.reason());
    }

    @GetMapping("/lookup")
    public CredentialResponse lookup(
            @RequestParam String tenantId, @RequestParam String credentialNumber) {
        return CredentialResponse.from(getCredential.byNumber(tenantId, credentialNumber));
    }

    @GetMapping("/by-app/{developerAppId}")
    public List<CredentialResponse> byApp(@PathVariable String developerAppId) {
        return getCredential.byApp(developerAppId).stream().map(CredentialResponse::from).toList();
    }

    @GetMapping("/{apiCredentialId}")
    public CredentialResponse get(@PathVariable String apiCredentialId) {
        return CredentialResponse.from(getCredential.byId(apiCredentialId));
    }

    public record IssueRequest(
            @NotBlank String tenantId,
            @NotBlank String developerAppId,
            @NotBlank @Size(max = 120) String label,
            String expiresAt) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record IssuedCredentialResponse(String apiCredentialId, String apiKey, String keyPrefix) {}

    public record CredentialResponse(
            String apiCredentialId,
            String tenantId,
            String credentialNumber,
            String developerAppId,
            String keyPrefix,
            String label,
            String expiresAt,
            String status) {

        static CredentialResponse from(ApiCredential credential) {
            return new CredentialResponse(
                    credential.id().value(),
                    credential.tenantId().value(),
                    credential.credentialNumber().value(),
                    credential.developerAppId().value(),
                    credential.keyPrefix(),
                    credential.label(),
                    credential.expiresAt().map(Object::toString).orElse(null),
                    credential.status().name());
        }
    }
}
