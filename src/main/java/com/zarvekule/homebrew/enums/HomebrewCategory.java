package com.zarvekule.homebrew.enums;

public enum HomebrewCategory {
    ARMOR("Zırh"),
    WEAPON("Silah"),
    SPELL("Büyü"),
    MONSTER("Canavar"),
    RACE("Irk"),
    CLASS("Sınıf"),
    BACKGROUND("Geçmiş"),
    FEAT("Hüner"),
    CONDITION("Durum"),
    PLANE("Düzlem"),
    MAGIC_ITEM("Sihirli Eşya"),
    CUSTOM("Özel");

    private final String displayName;

    HomebrewCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}