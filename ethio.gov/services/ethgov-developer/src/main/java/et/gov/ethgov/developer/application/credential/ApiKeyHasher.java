package et.gov.ethgov.developer.application.credential;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

public final class ApiKeyHasher {

    private ApiKeyHasher() {}

    public static GeneratedKey generate() {
        String secret = "egk_" + UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String prefix = secret.substring(0, 12);
        return new GeneratedKey(secret, prefix, sha256Hex(secret));
    }

    public static String sha256Hex(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }

    public record GeneratedKey(String secret, String prefix, String hash) {}
}
