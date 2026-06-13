package enums;

public enum CategoryEnum {

    ELEKTRONIK("Elektronik"),
    GIYIM("Giyim"),
    GIDA("Gıda"),
    OFIS("Ofis Malzemeleri"),
    DIGER("Diğer");

    private final String displayName;

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
