package enums;

public enum RoleEnum {

    ADMIN("yonetici"),
    PERSONEL("personel");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
