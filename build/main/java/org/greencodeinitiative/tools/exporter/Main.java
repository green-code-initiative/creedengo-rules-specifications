package org.greencodeinitiative.tools.exporter;

import org.greencodeinitiative.tools.exporter.infra.MetadataWriter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class Main implements Runnable {
    private final List<String> args;

    public Main(String[] args) {
        this.args = ofNullable(args).map(List::of).orElse(emptyList());
    }

    public static void main(String... args) {
        new Main(args).run();
    }

    @Override
    public void run() {
        Path sourceDir = argAsPath(0, "sourceDir");
        Path targetDir = argAsPath(1, "targetDir");
        new MetadataWriter(
                sourceDir,
                targetDir,
                Map.of(
                        "title", arg(2, "specificationTitle"),
                        "version", arg(3, "specificationVersion"),
                        "scmRevisionNumber", arg(4, "specificationSCMRevisionNumber"),
                        "scmRevisionDate", arg(5, "specificationSCMRevisionDate")
                ),
                // indexFile
                optionalArg(6)
                        .map(Path::of)
                        .orElseGet(() -> targetDir.resolve("index.json")),
                // minTermLength
                optionalArg(7)
                        .map(Integer::parseInt)
                        .orElse(4)
        ).run();
    }

    private Optional<String> optionalArg(int index) {
        if (args.size() <= index) {
            return empty();
        }
        return Optional.of(args.get(index));
    }

    private String arg(int index, String description) {
        if (args.size() <= index) {
            throw new IllegalArgumentException("argument " + (index + 1) + " is required: " + description);
        }
        return optionalArg(index).orElseThrow(() -> new IllegalArgumentException("argument " + (index + 1) + " is required: " + description));
    }

    private Path argAsPath(int index, String description) {
        return Path.of(arg(index, description));
    }
}
