package com.africahr.leave.model.enums;

public enum UserRole {
    USER,
    MANAGER,
    ADMIN;

    public boolean hasHigherOrEqualPrivilege(UserRole other) {
        return this.ordinal() >= other.ordinal();
    }
} 