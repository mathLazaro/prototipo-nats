package com.github.mathlazaro.model;

import java.io.ByteArrayInputStream;

public record RequestFileDTO(String fileName, ByteArrayInputStream data) {
}
