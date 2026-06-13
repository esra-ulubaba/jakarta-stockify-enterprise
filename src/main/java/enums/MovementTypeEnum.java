package enums;

public enum MovementTypeEnum {

    GIRIS("Stok Girişi"),
    CIKIS("Stok Çıkışı"),
    DUZELTME("Düzeltme");

    private final String displayName;

    MovementTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

