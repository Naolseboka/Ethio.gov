package et.gov.ethgov.platform.commons.address;

/**
 * First-level administrative division of Ethiopia (regional state or chartered city).
 */
public enum RegionalState {
    ADDIS_ABABA("AA", "Addis Ababa"),
    AFAR("AF", "Afar"),
    AMHARA("AM", "Amhara"),
    BENISHANGUL_GUMUZ("BG", "Benishangul-Gumuz"),
    DIRE_DAWA("DD", "Dire Dawa"),
    GAMBELA("GA", "Gambela"),
    HARARI("HA", "Harari"),
    OROMIA("OR", "Oromia"),
    SIDAMA("SI", "Sidama"),
    SOMALI("SO", "Somali"),
    SOUTH_ETHIOPIA("SE", "South Ethiopia"),
    SOUTH_WEST_ETHIOPIA("SW", "South West Ethiopia"),
    CENTRAL_ETHIOPIA("CE", "Central Ethiopia"),
    TIGRAY("TI", "Tigray");

    private final String code;
    private final String englishName;

    RegionalState(String code, String englishName) {
        this.code = code;
        this.englishName = englishName;
    }

    public String code() {
        return code;
    }

    public String englishName() {
        return englishName;
    }

    public static RegionalState fromCode(String code) {
        for (RegionalState state : values()) {
            if (state.code.equalsIgnoreCase(code)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown regional state code: " + code);
    }
}
