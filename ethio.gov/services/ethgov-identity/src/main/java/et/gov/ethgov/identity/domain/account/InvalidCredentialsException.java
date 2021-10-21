package et.gov.ethgov.identity.domain.account;

import et.gov.ethgov.platform.commons.error.DomainException;

public final class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid Credentials", "Username or password is incorrect", 401);
    }
}
