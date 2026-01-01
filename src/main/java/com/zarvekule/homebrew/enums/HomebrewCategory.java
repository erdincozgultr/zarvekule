package com.zarvekule.homebrew.enums;

public enum HomebrewCategory {
    BACKGROUND("Geçmiş"),
    SPELLS("Büyüler"),
    MAGIC_ITEM("Sihirli Eşyalar"),
    FEATS("Yetenekler"),
    MONSTERS("Canavarlar"),
    PLANES("Düzlemler"),
    WEAPON("Silahlar"),
    RACES("Irklar"),
    CONDITIONS("Durumlar"),
    ARMOR("Zırhlar"),
    CLASSES("Sınıflar"),
    CUSTOM("Özel");

    private final String displayName;

    HomebrewCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}