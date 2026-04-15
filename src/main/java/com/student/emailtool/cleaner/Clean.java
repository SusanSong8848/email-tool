package com.student.emailtool.cleaner;

import com.student.emailtool.util.EmailValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Clean {
    private static final String DEFAULT_OUT = "-";

    private static String inputFile = null;
    private static String outputFile = DEFAULT_OUT;
    private static String badFile = null;
    private static String mode = "clean";
    private static String format = "formal";
    private static boolean dedup = false;
    private static String logFile = null;
    private static boolean verbose = false;

    public static void main(String[] args) throws IOException {
        parseArgs(args);
        if (inputFile == null) {
            System.err.println("Missing -i input file");
            System.exit(1);
        }

        List<String> lines = Files.readAllLines(Path.of(inputFile));
        List<String> goodLines = new ArrayList<>();
        List<String> badLines = new ArrayList<>();
        Set<String> emailSet = new LinkedHashSet<>();

        for (String raw : lines) {
            String[] fields = splitLine(raw);
            if (fields.length != 3) {
                badLines.add(raw);
                continue;
            }

            String name = fields[0].trim();
            String email = fields[1].trim();
            String city = fields[2].trim();

            if (!EmailValidator.isValid(email)) {
                badLines.add(raw);
                continue;
            }

            if ("clean".equals(mode)) {
                if ("formal".equals(format)) {
                    name = capitalizeWords(name);
                    email = email.toLowerCase();
                    city = capitalizeWords(city);
                } else if ("lower".equals(format)) {
                    name = name.toLowerCase();
                    email = email.toLowerCase();
                    city = city.toLowerCase();
                }
                goodLines.add(String.join(",", name, email, city));
            } else if ("emails".equals(mode)) {
                email = email.toLowerCase();
                if (dedup) {
                    if (emailSet.add(email)) {
                        goodLines.add(email);
                    }
                } else {
                    goodLines.add(email);
                }
            }
        }

        if (!DEFAULT_OUT.equals(outputFile)) {
            Files.write(Path.of(outputFile), goodLines);
        } else {
            goodLines.forEach(System.out::println);
        }

        if (badFile != null && !badLines.isEmpty()) {
            Files.write(Path.of(badFile), badLines);
        }

        if (logFile != null || verbose) {
            String logMsg = String.format(
                    "Input: %s, Good lines: %d, Bad lines: %d, Mode: %s, Format: %s, Dedup: %b",
                    inputFile, goodLines.size(), badLines.size(), mode, format, dedup
            );
            if (logFile != null) {
                Files.writeString(
                        Path.of(logFile),
                        logMsg + System.lineSeparator(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
            if (verbose) {
                System.err.println(logMsg);
            }
        }
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i" -> inputFile = nextArg(args, ++i, "-i");
                case "-o" -> outputFile = nextArg(args, ++i, "-o");
                case "--bad" -> badFile = nextArg(args, ++i, "--bad");
                case "--mode" -> mode = nextArg(args, ++i, "--mode");
                case "--format" -> format = nextArg(args, ++i, "--format");
                case "--dedup" -> dedup = true;
                case "--log" -> logFile = nextArg(args, ++i, "--log");
                case "--verbose" -> verbose = true;
                default -> System.err.println("Unknown option: " + args[i]);
            }
        }
    }

    private static String nextArg(String[] args, int index, String option) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for option: " + option);
        }
        return args[index];
    }

    private static String[] splitLine(String line) {
        if (line.contains(",")) {
            return line.split(",");
        }
        if (line.contains(";")) {
            return line.split(";");
        }
        if (line.contains("|")) {
            return line.split("\\|");
        }
        return new String[0];
    }

    private static String capitalizeWords(String value) {
        String[] words = value.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1).toLowerCase())
              .append(" ");
        }
        return sb.toString().trim();
    }
}
