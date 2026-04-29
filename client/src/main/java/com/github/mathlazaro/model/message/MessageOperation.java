package com.github.mathlazaro.model.message;

import lombok.Getter;

public enum MessageOperation {
    GREETINGS("greetings"),
    JOKE("jokes");

    @Getter
    private final String subject;

    MessageOperation(String subject) {
        this.subject = subject;
    }

}
