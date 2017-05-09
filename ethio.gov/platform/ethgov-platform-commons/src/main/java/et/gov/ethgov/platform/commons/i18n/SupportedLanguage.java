package et.gov.ethgov.platform.commons.i18n;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Languages officially supported by EthGov citizen-facing surfaces.
 */
public enum SupportedLanguage {
    ENGLISH("en", "eng", Locale.ENGLISH),
    AMHARIC("am", "amh", Locale.forLanguageTag("am")),
    AFAAN_OROMO("om", "orm", Locale.forLanguageTag("om")),
    TIGRINYA("ti", "tir", Locale.forLanguageTag("ti")),
    SIDAMA("sid", "sid", Locale.forLanguageTag("sid")),
    SOMALI("so", "som", Locale.forLanguageTag("so"));

    private final String iso639_1;
    private final String iso639_2;
    private final Locale locale;

    SupportedLanguage(String iso639_1, String iso639_2, Locale locale) {
        this.iso639_1 = iso639_1;
        this.iso639_2 = iso639_2;
        this.locale = locale;
    }

    public String iso639_1() {
        return iso639_1;
    }

    public String iso639_2() {
        return iso639_2;
    }

    public Locale locale() {
        return locale;
    }

    public static SupportedLanguage defaultLanguage() {
        return ENGLISH;
    }

    public static Optional<SupportedLanguage> fromTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return Optional.empty();
        }
        String normalized = tag.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        String primary = normalized.split("-", 2)[0];
        return Arrays.stream(values())
                .filter(lang -> lang.iso639_1.equals(primary) || lang.iso639_2.equals(primary))
                .findFirst();
    }

    public static SupportedLanguage requireFromTag(String tag) {
        return fromTag(tag)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language tag: " + tag));
    }
}
