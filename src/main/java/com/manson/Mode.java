package com.manson;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

enum Mode {
    PATCH("patch"), CONFIG("config");

    private final String value;

    Mode(String value) {
        this.value = value;
    }

    public static Mode fromString(String input) {
        return Arrays.stream(Mode.values()).filter(
            x -> StringUtils.equalsIgnoreCase(x.value, input) || StringUtils.equalsIgnoreCase(x.name(), input))
            .findFirst().orElse(null);
    }
}
