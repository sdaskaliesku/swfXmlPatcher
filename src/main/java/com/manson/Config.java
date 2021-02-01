package com.manson;

import java.util.List;
import lombok.Data;

@Data
public class Config {
    private String input;
    private String output;
    private List<XmlConfig> configs;
}
