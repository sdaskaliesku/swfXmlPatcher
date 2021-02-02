package com.manson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.Source;
import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.DifferenceEngine;

public class Main {

    private static final ObjectMapper om = new ObjectMapper();

    private static final List<String> IGNORED_XML_FIELDS = Arrays.asList("fileOffset");

    private static Options createOptions() {
        Options options = new Options();

        Option mode = new Option("m", "mode", true, "Program mode: `patch` or `config`");
        mode.setRequired(true);

        Option input = new Option("in", "input", true, "Input xml file path");
        Option output = new Option("out", "output", true, "Output xml file path");


        Option original = new Option("orig", "original", true, "Original xml file path");
        Option patched = new Option("patched", "patched", true, "Patched xml file path");

        Option config = new Option("c", "config", true, "Config file path for patch mode or config output file path for config mode");


        options.addOption(input);
        options.addOption(output);

        options.addOption(original);
        options.addOption(patched);

        options.addOption(config);

        options.addOption(mode);
        return options;
    }

    private static Config parseConfig(String path) {
        try {
            return om.readValue(new File(path), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static void printHelpAndExit(HelpFormatter formatter, Options options) {
        formatter.printHelp("swfXmlPatcher", options);
        System.exit(1);
    }

    private static CommandLine parseArgs(CommandLineParser parser, Options options, String[] args,
        HelpFormatter formatter) {
        try {
            return parser.parse(options, args, true);
        } catch (Exception e) {
            e.printStackTrace();
            printHelpAndExit(formatter, options);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = createOptions();
        CommandLine cmd = parseArgs(parser, options, args, formatter);
        if (cmd == null) {
            return;
        }
        Mode mode = Mode.fromString(cmd.getOptionValue("mode"));
        if (Objects.isNull(mode)) {
            printHelpAndExit(formatter, options);
            return;
        }
        switch (mode) {
            case PATCH:
                PatchArguments inputArguments = parsePatchArguments(cmd);
                if (!inputArguments.isValid()) {
                    printHelpAndExit(formatter, options);
                }
                XmlPatcherService xmlPatcherService = new XmlPatcherService(inputArguments);
                xmlPatcherService.parse();
                break;
            case CONFIG:
                ConfigArguments configArguments = parseConfigArguments(cmd);
                if (!configArguments.isValid()) {
                    printHelpAndExit(formatter, options);
                }
                Config config = buildConfig(configArguments.getOriginal(), configArguments.getPatched());
                om.writerWithDefaultPrettyPrinter().writeValue(new File(configArguments.getOutputConfig()), config);
        }
    }

    private static Config buildConfig(String original, String patched) {
        Config config = new Config();
        List<XmlConfig> configs = new ArrayList<>();
        Source originalXml = Input.fromFile(original).build();
        Source patchedXml = Input.fromFile(patched).build();
        DifferenceEngine diff = new DOMDifferenceEngine();
        diff.addDifferenceListener((comparison, outcome) -> {
            String xpath = comparison.getControlDetails().getXPath();
            boolean containsIgnoredField = IGNORED_XML_FIELDS.stream().anyMatch(x -> StringUtils.containsIgnoreCase(xpath, x));
            if (containsIgnoredField) {
                return;
            }
            XmlConfig xmlConfig = new XmlConfig();
            xmlConfig.setPath(comparison.getControlDetails().getXPath());
            xmlConfig.setValue(String.valueOf(comparison.getTestDetails().getValue()));
            configs.add(xmlConfig);
        });
        diff.compare(originalXml, patchedXml);
        config.setConfigs(configs);
        return config;
    }

    private static ConfigArguments parseConfigArguments(CommandLine cmd) {
        ConfigArguments arguments = new ConfigArguments();
        String original = cmd.getOptionValue("original");
        String patched = cmd.getOptionValue("patched");
        String configFile = cmd.getOptionValue("config");

        arguments.setOriginal(original);
        arguments.setPatched(patched);
        arguments.setOutputConfig(configFile);
        return arguments;
    }

    private static PatchArguments parsePatchArguments(CommandLine cmd) {
        PatchArguments arguments = new PatchArguments();
        String inputFile = cmd.getOptionValue("input");
        String outputFile = cmd.getOptionValue("output");
        String configFile = cmd.getOptionValue("config");

        arguments.setInputFile(inputFile);
        arguments.setOutputFile(outputFile);
        arguments.setConfigFile(configFile);
        return arguments;
    }


    @Data
    public static class PatchArguments {

        private String inputFile;
        private String outputFile;
        private String configFile;

        public Config getConfig() {
            return parseConfig(configFile);
        }

        public boolean isValid() {
            return StringUtils.isNoneBlank(inputFile, outputFile, configFile);
        }
    }

}
