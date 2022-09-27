package com.andrew.wechetshop.api.entity;

public enum DataStatus {
    OK(),
    DELETED(),

    // only 4 order
    PENDING(),
    PAID(),
    DELIVERED(), // 物流中
    RECEIVED(); // 已收获

    public String getName() {
        return this.name().toLowerCase();
    }

    public static DataStatus fromName(String name) {
        try {
            if (name == null) {
                return null;
            }
            return DataStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
