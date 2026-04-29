package com.github.mathlazaro.model.file;

import lombok.Getter;

public enum FileOperation {
    SAVE("file.save"),
    READ("file.read"),
    APPEND("file.append"),
    DELETE("file.delete");

    @Getter
    private final String subject;

    FileOperation(String subject) {
        this.subject = subject;
    }

}

