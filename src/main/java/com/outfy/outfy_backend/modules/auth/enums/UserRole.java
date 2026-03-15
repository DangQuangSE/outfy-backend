package com.outfy.outfy_backend.modules.auth.enums;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN"),
    SELLER("SELLER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid UserRole: " + value);
    }
}

