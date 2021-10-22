package et.gov.ethgov.identity.domain.account;

/**
 * Port for password hashing so the domain never depends on Spring Security crypto APIs.
 */
public interface PasswordHasher {

    String hash(String plaintext);

    boolean matches(String plaintext, String hash);
}
