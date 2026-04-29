package com.github.mathlazaro.model.message;

import lombok.Getter;

public enum JokeType {
    GENERAL("general"),
    KNOCK_KNOCK("knock-knock"),
    PROGRAMMING("programming"),
    DAD("dad"),
    ANY("any");

    @Getter
    private final String subject;

    JokeType(String subject) {
        this.subject = subject;
    }

}
