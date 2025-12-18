package com.zarvekule.gamification.enums;

import lombok.Getter;

@Getter
public enum ActionType {
    DAILY_LOGIN(10),
    POST_COMMENT(5),
    CREATE_HOMEBREW(50),
    CREATE_BLOG(30),
    CREATE_CAMPAIGN(40),
    RECEIVE_LIKE(2);

    private final int xpValue;

    ActionType(int xpValue) {
        this.xpValue = xpValue;
    }
}