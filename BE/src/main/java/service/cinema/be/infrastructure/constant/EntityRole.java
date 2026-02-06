package service.cinema.be.infrastructure.constant;

public enum EntityRole {
    ADMIN("ROLE_ADMIN"),
    STAFF("ROLE_STAFF"),
    CUSTOMER("ROLE_CUSTOMER");

    private final String authority;

    EntityRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public String getRoleName() {
        return this.name();
    }
}
