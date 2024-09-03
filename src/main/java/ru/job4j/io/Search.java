package ru.job4j.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Search {

    public static void main(String[] args) throws IOException {

        ArgsName searchArgs = ArgsName.of(args);
        String[] argsSearch = new String[4];
        argsSearch[0] = searchArgs.get("d");
        argsSearch[1] = searchArgs.get("n");
        argsSearch[2] = searchArgs.get("t");
        argsSearch[3] = searchArgs.get("o");

        Path start = Paths.get(searchArgs.get("d"));
        validate(argsSearch);
        List<Path> data = new ArrayList<>();
        if ("mask".equals(argsSearch[2])) {
            String regex = Search.toRegex(argsSearch[1]);
            data = search(start, path -> path.toFile().getName().matches(regex));
        }
        if ("name".equals(argsSearch[2])) {
            data = search(start, path -> path.toFile().getName().endsWith(argsSearch[1]));
        }
        saveTo(data, argsSearch[3]);
    }

    public static void saveTo(List<Path> data, String out) {
        try (PrintWriter output = new PrintWriter(
                new BufferedOutputStream(
                        new FileOutputStream(out)
                ))) {
            data.forEach(output::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    public static void validate(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("You need input root folder "
                    + ", file extension for search"
                    + ", type of search (mask or name)"
                    + ", the name of the file to be recorded !!!");
        }
        if (!Files.isDirectory(Path.of(args[0]))) {
            throw new IllegalArgumentException(args[0] + " isn't directory !!!");
        }
        if (!"mask".equals(args[2]) && !"name".equals(args[2])) {
            throw new IllegalArgumentException("parameter '-n', must take values 'name' or 'mask' !!!");
        }
        String regex = "^[\\w,\\s-]+\\.[A-Za-z]{3}$";
        if (!args[3].matches(regex)) {
            throw new IllegalArgumentException("parameter '-o' invalid. Name file is not valid !!!");
        }
    }

    public static String toRegex(String str) {
        return str.replace("*", "\\w+")
                .replace(".", "\\.")
                .replace("?", ".");
    }
}