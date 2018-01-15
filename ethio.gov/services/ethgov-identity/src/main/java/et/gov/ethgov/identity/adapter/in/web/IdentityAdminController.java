package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.account.AssignTenantMembershipUseCase;
import et.gov.ethgov.identity.application.account.DisableAccountUseCase;
import et.gov.ethgov.identity.application.account.RegisterCitizenUseCase;
import et.gov.ethgov.identity.application.account.RegisterPublicEmployeeUseCase;
import et.gov.ethgov.identity.application.client.RegisterOAuthClientUseCase;
import et.gov.ethgov.identity.domain.account.UserAccountId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class IdentityAdminController {

    private final RegisterPublicEmployeeUseCase registerPublicEmployee;
    private final RegisterCitizenUseCase registerCitizen;
    private final AssignTenantMembershipUseCase assignTenantMembership;
    private final DisableAccountUseCase disableAccount;
    private final RegisterOAuthClientUseCase registerOAuthClient;

    public IdentityAdminController(
            RegisterPublicEmployeeUseCase registerPublicEmployee,
            RegisterCitizenUseCase registerCitizen,
            AssignTenantMembershipUseCase assignTenantMembership,
            DisableAccountUseCase disableAccount,
            RegisterOAuthClientUseCase registerOAuthClient) {
        this.registerPublicEmployee = registerPublicEmployee;
        this.registerCitizen = registerCitizen;
        this.assignTenantMembership = assignTenantMembership;
        this.disableAccount = disableAccount;
        this.registerOAuthClient = registerOAuthClient;
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
        UserAccountId id = registerPublicEmployee.execute(new RegisterPublicEmployeeUseCase.Command(
                request.username(),
                request.password(),
                request.tenantId(),
                request.roles(),
                request.permissions()));
        return new AccountResponse(id.value());
    }

    @PostMapping("/citizens")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse registerCitizen(@Valid @RequestBody RegisterCitizenRequest request) {
        UserAccountId id =
                registerCitizen.execute(new RegisterCitizenUseCase.Command(request.username(), request.password()));
        return new AccountResponse(id.value());
    }

    @PostMapping("/accounts/{accountId}/memberships")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignMembership(
            @PathVariable String accountId, @Valid @RequestBody AssignMembershipRequest request) {
        assignTenantMembership.execute(new AssignTenantMembershipUseCase.Command(
                accountId, request.tenantId(), request.roles(), request.permissions()));
    }

    @PostMapping("/accounts/{accountId}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable String accountId) {
        disableAccount.execute(accountId);
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody RegisterClientRequest request) {
        RegisterOAuthClientUseCase.Result result = registerOAuthClient.execute(new RegisterOAuthClientUseCase.Command(
                request.clientId(),
                request.clientSecret(),
                request.tenantId(),
                request.redirectUris(),
                request.grantTypes(),
                request.scopes()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ClientResponse(result.id().value(), result.clientId(), result.clientSecret()));
    }

    public record RegisterEmployeeRequest(
            @NotBlank String username,
            @NotBlank @Size(min = 12, max = 128) String password,
            @NotBlank String tenantId,
            @NotEmpty Set<String> roles,
            Set<String> permissions) {
        public RegisterEmployeeRequest {
            if (permissions == null) {
                permissions = Set.of();
            }
        }
    }

    public record RegisterCitizenRequest(
            @NotBlank String username, @NotBlank @Size(min = 12, max = 128) String password) {}

    public record AssignMembershipRequest(
            @NotBlank String tenantId, @NotEmpty Set<String> roles, Set<String> permissions) {
        public AssignMembershipRequest {
            if (permissions == null) {
                permissions = Set.of();
            }
        }
    }

    public record RegisterClientRequest(
            @NotBlank String clientId,
            String clientSecret,
            @NotBlank String tenantId,
            @NotEmpty Set<String> redirectUris,
            @NotEmpty Set<String> grantTypes,
            @NotEmpty Set<String> scopes) {}

    public record AccountResponse(String accountId) {}

    public record ClientResponse(String id, String clientId, String clientSecret) {}
}
