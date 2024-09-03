package ru.job4j.io;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {

    private String key;
    private String value;
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            String error = String.format("This key: '%s' is missing", key);
            throw new IllegalArgumentException(error);
        }
        return values.get(key);
    }

    private void validate(String arg) {
        if (!arg.contains("=")) {
            String error = String.format("Error: This argument '%s' does not contain an equal sign", arg);
            throw new IllegalArgumentException(error);
        }
        if (!arg.startsWith("-")) {
            String error = String.format("Error: This argument '%s' does not start with a '-' character", arg);
            throw new IllegalArgumentException(error);
        }
        String[] parts = arg.split("=", 2);
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid argument");
        }
        key = parts[0].replaceFirst("-", "");
        value = parts[1];
        if (value.length() == 0) {
            String error = String.format("Error: This argument '%s' does not contain a value", arg);
            throw new IllegalArgumentException(error);
        }
        if (key.length() == 0) {
            String error = String.format("Error: This argument '%s' does not contain a key", arg);
            throw new IllegalArgumentException(error);
        }
    }

    private void parse(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String arg : args) {
            validate(arg);
            values.put(key, value);
        }
    }

    public static ArgsName of(String[] args) {
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }

}