package com.zarvekule.gamification.enums;

import lombok.Getter;

@Getter
public enum RankTier {
    PEASANT("Köylü", 0),
    ADVENTURER("Maceracı", 100),
    VETERAN("Kıdemli", 500),
    HERO("Kahraman", 1500),
    LEGEND("Efsane", 5000);

    private final String title;
    private final int minXp;

    RankTier(String title, int minXp) {
        this.title = title;
        this.minXp = minXp;
    }

    public static RankTier getRankByXp(long xp) {
        RankTier currentRank = PEASANT;
        for (RankTier rank : values()) {
            if (xp >= rank.minXp) {
                currentRank = rank;
            }
        }
        return currentRank;
    }
}