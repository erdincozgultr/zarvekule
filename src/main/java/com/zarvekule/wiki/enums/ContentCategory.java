package com.zarvekule.wiki.enums;

public enum ContentCategory {
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
    SPELL_LIST("Büyü Listesi"),
    CLASSES("Sınıflar"),
    DOCUMENTS("Dökümanlar"),
    SECTIONS("Kısımlar");

    private final String displayName;

    ContentCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
