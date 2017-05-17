package et.gov.ethgov.platform.commons.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SupportedLanguageTest {

    @Test
    void resolvesSupportedTags() {
        assertThat(SupportedLanguage.fromTag("am")).contains(SupportedLanguage.AMHARIC);
        assertThat(SupportedLanguage.fromTag("om-ET")).contains(SupportedLanguage.AFAAN_OROMO);
        assertThat(SupportedLanguage.fromTag("tir")).contains(SupportedLanguage.TIGRINYA);
        assertThat(SupportedLanguage.fromTag("xx")).isEmpty();
    }
}
