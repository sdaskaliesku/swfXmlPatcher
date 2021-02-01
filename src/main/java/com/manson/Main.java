package com.manson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Main {

    private static final ObjectMapper om = new ObjectMapper();

    private static Options createOptions() {
        Options options = new Options();
        Option input = new Option("i", "input", true, "Input file path");
        input.setRequired(true);
        options.addOption(input);
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

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        Options options = createOptions();
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            e.printStackTrace();
            formatter.printHelp("swfXmlPatcher", options);
            System.exit(1);
        }

        String inputFile = cmd.getOptionValue("input");
        Config config = parseConfig(inputFile);
        XmlPatcherService xmlPatcherService = new XmlPatcherService(config);
        xmlPatcherService.parse();
    }

}
