package com.manson;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ConfigArguments {

    private String original;
    private String patched;
    private String outputConfig;

    public boolean isValid() {
        return StringUtils.isNoneBlank(original, patched, outputConfig);
    }

}
